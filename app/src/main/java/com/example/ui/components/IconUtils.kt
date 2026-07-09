package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

fun getSubjectIcon(name: String): ImageVector {
    return when (name) {
        "School" -> Icons.Default.School
        "MenuBook" -> Icons.AutoMirrored.Filled.MenuBook
        "Functions" -> Icons.Default.Functions
        "Biotech" -> Icons.Default.Biotech
        "HistoryEdu" -> Icons.Default.HistoryEdu
        "Public" -> Icons.Default.Public
        "Language" -> Icons.Default.Language
        else -> Icons.Default.Book
    }
}
