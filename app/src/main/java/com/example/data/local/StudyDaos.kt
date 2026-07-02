package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks")
    suspend fun clearAll()
}

@Dao
interface ClassDao {
    @Query("SELECT * FROM classes ORDER BY id ASC")
    fun getAllClasses(): Flow<List<ClassEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classEvent: ClassEvent)

    @Delete
    suspend fun deleteClass(classEvent: ClassEvent)

    @Query("DELETE FROM classes")
    suspend fun clearAll()
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<FinanceTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinanceTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: FinanceTransaction)

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}

@Dao
interface StudyNoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): Flow<List<StudyNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StudyNote)

    @Delete
    suspend fun deleteNote(note: StudyNote)

    @Query("DELETE FROM notes")
    suspend fun clearAll()
}

@Dao
interface CourseGradeDao {
    @Query("SELECT * FROM course_grades ORDER BY id DESC")
    fun getAllGrades(): Flow<List<CourseGrade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: CourseGrade)

    @Delete
    suspend fun deleteGrade(grade: CourseGrade)

    @Query("DELETE FROM course_grades")
    suspend fun clearAll()
}

@Dao
interface StudyResourceDao {
    @Query("SELECT * FROM study_resources ORDER BY id DESC")
    fun getAllResources(): Flow<List<StudyResource>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: StudyResource)

    @Delete
    suspend fun deleteResource(resource: StudyResource)

    @Query("DELETE FROM study_resources")
    suspend fun clearAll()
}

