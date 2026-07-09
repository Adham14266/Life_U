package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class StudyRepository(
    private val taskDao: TaskDao,
    private val classDao: ClassDao,
    private val transactionDao: TransactionDao,
    private val studyNoteDao: StudyNoteDao,
    private val courseGradeDao: CourseGradeDao,
    private val studyResourceDao: StudyResourceDao,
    private val userDao: UserDao
) {
    fun getAllTasks(userEmail: String): Flow<List<Task>> = taskDao.getAllTasks(userEmail)
    fun getAllClasses(userEmail: String): Flow<List<ClassEvent>> = classDao.getAllClasses(userEmail)
    fun getAllTransactions(userEmail: String): Flow<List<FinanceTransaction>> = transactionDao.getAllTransactions(userEmail)
    fun getAllNotes(userEmail: String): Flow<List<StudyNote>> = studyNoteDao.getAllNotes(userEmail)
    fun getAllGrades(userEmail: String): Flow<List<CourseGrade>> = courseGradeDao.getAllGrades(userEmail)
    fun getAllResources(userEmail: String): Flow<List<StudyResource>> = studyResourceDao.getAllResources(userEmail)

    // User operations
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)

    // Tasks operations
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    suspend fun clearTasks(userEmail: String) = taskDao.clearAll(userEmail)

    // Classes operations
    suspend fun insertClass(classEvent: ClassEvent) = classDao.insertClass(classEvent)
    suspend fun deleteClass(classEvent: ClassEvent) = classDao.deleteClass(classEvent)
    suspend fun clearClasses(userEmail: String) = classDao.clearAll(userEmail)

    // Transactions operations
    suspend fun insertTransaction(transaction: FinanceTransaction) = transactionDao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: FinanceTransaction) = transactionDao.deleteTransaction(transaction)
    suspend fun clearTransactions(userEmail: String) = transactionDao.clearAll(userEmail)

    // Notes operations
    suspend fun insertNote(note: StudyNote) = studyNoteDao.insertNote(note)
    suspend fun deleteNote(note: StudyNote) = studyNoteDao.deleteNote(note)
    suspend fun clearNotes(userEmail: String) = studyNoteDao.clearAll(userEmail)

    // Grades operations
    suspend fun insertGrade(grade: CourseGrade) = courseGradeDao.insertGrade(grade)
    suspend fun deleteGrade(grade: CourseGrade) = courseGradeDao.deleteGrade(grade)
    suspend fun clearGrades(userEmail: String) = courseGradeDao.clearAll(userEmail)

    // Resource operations
    suspend fun insertResource(resource: StudyResource) = studyResourceDao.insertResource(resource)
    suspend fun deleteResource(resource: StudyResource) = studyResourceDao.deleteResource(resource)
    suspend fun clearResources(userEmail: String) = studyResourceDao.clearAll(userEmail)
}
