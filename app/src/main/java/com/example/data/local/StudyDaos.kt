package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userEmail = :userEmail ORDER BY id DESC")
    fun getAllTasks(userEmail: String): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE userEmail = :userEmail")
    suspend fun clearAll(userEmail: String)
}

@Dao
interface ClassDao {
    @Query("SELECT * FROM classes WHERE userEmail = :userEmail ORDER BY id ASC")
    fun getAllClasses(userEmail: String): Flow<List<ClassEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classEvent: ClassEvent)

    @Update
    suspend fun updateClass(classEvent: ClassEvent)

    @Delete
    suspend fun deleteClass(classEvent: ClassEvent)

    @Query("DELETE FROM classes WHERE userEmail = :userEmail")
    suspend fun clearAll(userEmail: String)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userEmail = :userEmail ORDER BY timestamp DESC")
    fun getAllTransactions(userEmail: String): Flow<List<FinanceTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinanceTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: FinanceTransaction)

    @Query("DELETE FROM transactions WHERE userEmail = :userEmail")
    suspend fun clearAll(userEmail: String)
}

@Dao
interface StudyNoteDao {
    @Query("SELECT * FROM notes WHERE userEmail = :userEmail ORDER BY id DESC")
    fun getAllNotes(userEmail: String): Flow<List<StudyNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StudyNote)

    @Delete
    suspend fun deleteNote(note: StudyNote)

    @Query("DELETE FROM notes WHERE userEmail = :userEmail")
    suspend fun clearAll(userEmail: String)
}

@Dao
interface CourseGradeDao {
    @Query("SELECT * FROM course_grades WHERE userEmail = :userEmail ORDER BY id DESC")
    fun getAllGrades(userEmail: String): Flow<List<CourseGrade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: CourseGrade)

    @Delete
    suspend fun deleteGrade(grade: CourseGrade)

    @Query("DELETE FROM course_grades WHERE userEmail = :userEmail")
    suspend fun clearAll(userEmail: String)
}

@Dao
interface StudyResourceDao {
    @Query("SELECT * FROM study_resources WHERE userEmail = :userEmail ORDER BY id DESC")
    fun getAllResources(userEmail: String): Flow<List<StudyResource>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: StudyResource)

    @Delete
    suspend fun deleteResource(resource: StudyResource)

    @Query("DELETE FROM study_resources WHERE userEmail = :userEmail")
    suspend fun clearAll(userEmail: String)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)
}

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams WHERE userEmail = :userEmail ORDER BY examDate ASC")
    fun getAllExams(userEmail: String): Flow<List<Exam>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam)

    @Update
    suspend fun updateExam(exam: Exam)

    @Delete
    suspend fun deleteExam(exam: Exam)

    @Query("DELETE FROM exams WHERE userEmail = :userEmail")
    suspend fun clearAll(userEmail: String)
}
