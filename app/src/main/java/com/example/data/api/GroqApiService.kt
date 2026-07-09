package com.example.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GroqClient {

    private const val BASE_URL = "https://api.groq.com/openai/v1/chat/completions"

    val visionModels = listOf(
        "llama-3.2-90b-vision-preview",
        "llama-3.2-11b-vision-preview"
    )

    val textModels = listOf(
        "llama-3.1-70b-versatile",
        "llama-3.1-8b-instant",
        "mixtral-8x7b-32768"
    )

    fun allModels(): List<String> = visionModels + textModels

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val responseAdapter = moshi.adapter(GroqChatResponse::class.java)

    suspend fun sendMessage(
        apiKey: String,
        model: String,
        messages: List<GroqConversationMessage>,
        systemPrompt: String,
        temperature: Float,
        maxTokens: Int
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val jsonBody = buildRequestJson(model, messages, systemPrompt, temperature, maxTokens)
                val body = jsonBody.toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(BASE_URL)
                    .header("Authorization", "Bearer $apiKey")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val parsed = responseAdapter.fromJson(responseBody)
                    val text = parsed?.choices?.firstOrNull()?.message?.content
                    if (text != null) {
                        Result.success(text)
                    } else {
                        Result.failure(Exception("Empty response from Groq"))
                    }
                } else {
                    val errorMsg = try {
                        val err = moshi.adapter(GroqErrorBody::class.java)
                            .fromJson(responseBody)
                        err?.message ?: responseBody
                    } catch (_: Exception) {
                        responseBody
                    }
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun buildRequestJson(
        model: String,
        messages: List<GroqConversationMessage>,
        systemPrompt: String,
        temperature: Float,
        maxTokens: Int
    ): String {
        val sb = StringBuilder()
        sb.append("{\"model\":")
        sb.append(jsonEscape(model))
        sb.append(",\"messages\":[")

        // System message
        sb.append("{\"role\":\"system\",\"content\":")
        sb.append(jsonEscape(systemPrompt))
        sb.append("},")

        // Conversation messages
        for (msg in messages) {
            sb.append("{\"role\":")
            sb.append(jsonEscape(msg.role))
            sb.append(",\"content\":")
            msg.contentParts?.let { parts ->
                sb.append("[")
                for ((i, part) in parts.withIndex()) {
                    if (i > 0) sb.append(",")
                    sb.append("{\"type\":")
                    sb.append(jsonEscape(part.type))
                    if (part.type == "text") {
                        sb.append(",\"text\":")
                        sb.append(jsonEscape(part.text ?: ""))
                    } else if (part.type == "image_url") {
                        sb.append(",\"image_url\":{\"url\":")
                        sb.append(jsonEscape(part.imageUrl ?: ""))
                        sb.append("}")
                    }
                    sb.append("}")
                }
                sb.append("]")
            } ?: sb.append(jsonEscape(msg.textContent ?: ""))
            sb.append("},")
        }

        sb.setLength(sb.length - 1) // Remove trailing comma

        sb.append("],\"temperature\":")
        sb.append(temperature.toString())
        sb.append(",\"max_tokens\":")
        sb.append(maxTokens.toString())
        sb.append("}")

        return sb.toString()
    }

    private fun jsonEscape(s: String): String {
        val out = StringBuilder(s.length + 2)
        out.append('"')
        for (c in s) {
            when (c) {
                '"' -> out.append("\\\"")
                '\\' -> out.append("\\\\")
                '\b' -> out.append("\\b")
                '\u000C' -> out.append("\\f")
                '\n' -> out.append("\\n")
                '\r' -> out.append("\\r")
                '\t' -> out.append("\\t")
                else -> out.append(c)
            }
        }
        out.append('"')
        return out.toString()
    }
}

data class GroqConversationMessage(
    val role: String,
    val textContent: String? = null,
    val contentParts: List<GroqContentPart>? = null
)

data class GroqContentPart(
    val type: String,
    val text: String? = null,
    val imageUrl: String? = null
)
