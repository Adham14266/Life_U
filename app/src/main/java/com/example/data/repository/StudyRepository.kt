package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow

class StudyRepository(
    private val taskDao: TaskDao,
    private val classDao: ClassDao,
    private val transactionDao: TransactionDao,
    private val studyNoteDao: StudyNoteDao,
    private val courseGradeDao: CourseGradeDao,
    private val studyResourceDao: StudyResourceDao
) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val allClasses: Flow<List<ClassEvent>> = classDao.getAllClasses()
    val allTransactions: Flow<List<FinanceTransaction>> = transactionDao.getAllTransactions()
    val allNotes: Flow<List<StudyNote>> = studyNoteDao.getAllNotes()
    val allGrades: Flow<List<CourseGrade>> = courseGradeDao.getAllGrades()
    val allResources: Flow<List<StudyResource>> = studyResourceDao.getAllResources()

    // Tasks operations
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    suspend fun clearTasks() = taskDao.clearAll()

    // Classes operations
    suspend fun insertClass(classEvent: ClassEvent) = classDao.insertClass(classEvent)
    suspend fun deleteClass(classEvent: ClassEvent) = classDao.deleteClass(classEvent)
    suspend fun clearClasses() = classDao.clearAll()

    // Transactions operations
    suspend fun insertTransaction(transaction: FinanceTransaction) = transactionDao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: FinanceTransaction) = transactionDao.deleteTransaction(transaction)
    suspend fun clearTransactions() = transactionDao.clearAll()

    // Notes operations
    suspend fun insertNote(note: StudyNote) = studyNoteDao.insertNote(note)
    suspend fun deleteNote(note: StudyNote) = studyNoteDao.deleteNote(note)
    suspend fun clearNotes() = studyNoteDao.clearAll()

    // Grades operations
    suspend fun insertGrade(grade: CourseGrade) = courseGradeDao.insertGrade(grade)
    suspend fun deleteGrade(grade: CourseGrade) = courseGradeDao.deleteGrade(grade)
    suspend fun clearGrades() = courseGradeDao.clearAll()

    // Resource operations
    suspend fun insertResource(resource: StudyResource) = studyResourceDao.insertResource(resource)
    suspend fun deleteResource(resource: StudyResource) = studyResourceDao.deleteResource(resource)
    suspend fun clearResources() = studyResourceDao.clearAll()
}
