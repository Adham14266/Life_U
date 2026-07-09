package com.example.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroqChatResponse(
    val choices: List<GroqChoice>? = null,
    val error: GroqErrorBody? = null
)

@JsonClass(generateAdapter = true)
data class GroqChoice(
    val message: GroqResponseMessage? = null
)

@JsonClass(generateAdapter = true)
data class GroqResponseMessage(
    val content: String? = null
)

@JsonClass(generateAdapter = true)
data class GroqErrorBody(
    val message: String? = null
)
