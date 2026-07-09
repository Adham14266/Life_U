package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Task::class, ClassEvent::class, FinanceTransaction::class, StudyNote::class, CourseGrade::class, StudyResource::class, User::class, Exam::class, Subject::class],
    version = 12,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun classDao(): ClassDao
    abstract fun transactionDao(): TransactionDao
    abstract fun studyNoteDao(): StudyNoteDao
    abstract fun courseGradeDao(): CourseGradeDao
    abstract fun studyResourceDao(): StudyResourceDao
    abstract fun userDao(): UserDao
    abstract fun examDao(): ExamDao
    abstract fun subjectDao(): SubjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifeu_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
