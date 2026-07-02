package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val priority: String, // "High", "Medium", "Done"
    val isCompleted: Boolean = false,
    val category: String = "",
    val dueDate: String = ""
)

@Entity(tableName = "classes")
data class ClassEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val timeRange: String, // e.g. "10:00 AM - 11:30 AM"
    val dayOfWeek: String, // e.g. "Mon", "Tue", "Wed", "Thu", "Fri"
    val type: String = "Lecture" // "Lecture", "Lab", "Seminar"
)

@Entity(tableName = "transactions")
data class FinanceTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double, // Negative for expense, positive for income
    val category: String, // "Housing", "Food", "Transportation", "Entertainment", "Books", "Income"
    val dateText: String, // e.g. "Today • 12:45 PM"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class StudyNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val courseName: String,
    val dateCreated: String = ""
)

@Entity(tableName = "course_grades")
data class CourseGrade(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseName: String,
    val gradeLetter: String, // "A+", "A", "A-", "B+", etc.
    val creditHours: Int, // Credit value of the course (e.g. 3, 4)
    val term: String // Semester or quarter, e.g. "Fall 2025"
)

@Entity(tableName = "study_resources")
data class StudyResource(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val url: String,
    val category: String, // "Textbooks", "Study Materials", "Academic Articles", "Other"
    val notes: String = "",
    val courseName: String = "",
    val dateAdded: String = ""
)

