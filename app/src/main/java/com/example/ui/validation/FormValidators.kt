package com.example.ui.validation

import android.util.Patterns

fun isNonBlank(value: String): Boolean = value.trim().isNotEmpty()

fun isValidEmail(value: String): Boolean {
    val trimmed = value.trim()
    return trimmed.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
}

fun isStrongPassword(value: String): Boolean = value.trim().length >= 8

fun isPositiveNumber(value: String): Boolean = value.trim().toDoubleOrNull()?.let { it > 0.0 } == true

fun isNonNegativeNumber(value: String): Boolean = value.trim().toDoubleOrNull()?.let { it >= 0.0 } == true

fun isValidWebUrl(value: String): Boolean {
    val trimmed = value.trim()
    if (trimmed.isEmpty()) return false
    val normalized = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed else "https://$trimmed"
    return Patterns.WEB_URL.matcher(normalized).matches()
}

fun isValidDueDate(value: String): Boolean {
    val trimmed = value.trim()
    if (trimmed.isEmpty()) return true
    val simpleDatePattern = Regex("^(?i)(today|tomorrow|now|\\d{1,2}/\\d{1,2}(/\\d{2,4})?|[a-z]{3,9}\\s+\\d{1,2})$")
    return simpleDatePattern.matches(trimmed)
}
