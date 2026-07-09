package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoshiGenerateContentRequest(
    val contents: List<MoshiContent>,
    @param:Json(name = "generationConfig") val generationConfig: MoshiGenerationConfig? = null,
    @param:Json(name = "systemInstruction") val systemInstruction: MoshiContent? = null
)

@JsonClass(generateAdapter = true)
data class MoshiContent(
    val role: String? = null,
    val parts: List<MoshiPart>
)

@JsonClass(generateAdapter = true)
data class MoshiPart(
    val text: String? = null,
    @param:Json(name = "inline_data") val inlineData: MoshiInlineData? = null
)

@JsonClass(generateAdapter = true)
data class MoshiInlineData(
    @param:Json(name = "mime_type") val mimeType: String,
    val data: String // base64-encoded
)

@JsonClass(generateAdapter = true)
data class MoshiGenerationConfig(
    val temperature: Float? = null,
    @param:Json(name = "maxOutputTokens") val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class MoshiGenerateContentResponse(
    val candidates: List<MoshiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class MoshiCandidate(
    val content: MoshiContent? = null
)
