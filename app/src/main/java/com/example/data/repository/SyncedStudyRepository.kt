package com.example.data.repository

import android.util.Log
import com.example.data.api.*
import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import org.json.JSONObject
import retrofit2.HttpException

class SyncedStudyRepository(
    private val taskDao: TaskDao,
    private val classDao: ClassDao,
    private val transactionDao: TransactionDao,
    private val studyNoteDao: StudyNoteDao,
    private val courseGradeDao: CourseGradeDao,
    private val studyResourceDao: StudyResourceDao,
    private val userDao: UserDao,
    private val examDao: ExamDao,
    private val subjectDao: SubjectDao,
    private val apiService: StudyAppApiService
) {
    companion object {
        private const val TAG = "SyncedStudyRepository"

        fun errorMessage(e: Exception): String {
            if (e is HttpException) {
                try {
                    val body = e.response()?.errorBody()?.string()
                    if (body != null) {
                        val json = JSONObject(body)
                        val msg = json.optString("message")
                        if (msg.isNotEmpty()) return msg
                    }
                } catch (_: Exception) { }
            }
            return e.message ?: "Unknown error"
        }
    }

    private var authToken: String? = null
    private val userEmailFlow = MutableStateFlow<String?>(null)

    fun setAuthToken(token: String, email: String? = null) {
        authToken = token
        email?.let { userEmailFlow.value = it }
    }

    fun getAuthToken(): String? = authToken

    // Auth operations
    suspend fun register(email: String, password: String, name: String): AuthResponseDto {
        return try {
            val response = apiService.register(RegisterDto(username = name, email = email, password = password))
            authToken = response.token
            userEmailFlow.value = email
            response
        } catch (e: Exception) {
            Log.e(TAG, "Register failed", e)
            throw e
        }
    }

    suspend fun login(email: String, password: String): AuthResponseDto {
        return try {
            // Using a trimmed email for the identifier fields as well
            val trimmedEmail = email.trim()
            val response = apiService.login(
                LoginDto(
                    usernameOrEmail = trimmedEmail,
                    email = trimmedEmail,
                    identifier = trimmedEmail,
                    password = password
                )
            )
            authToken = response.token
            userEmailFlow.value = response.email
            response
        } catch (e: Exception) {
            Log.e(TAG, "Login failed for email: $email", e)
            throw e
        }
    }

    suspend fun googleLogin(idToken: String, email: String?, name: String?): AuthResponseDto {
        return try {
            val response = apiService.googleLogin(GoogleLoginDto(idToken = idToken, email = email, name = name))
            authToken = response.token
            userEmailFlow.value = response.email
            response
        } catch (e: Exception) {
            Log.e(TAG, "Google login failed", e)
            throw e
        }
    }

    suspend fun getProfile(): Result<UserDto> = try {
        val token = authToken ?: throw Exception("Not authenticated")
        val user = apiService.getProfile("Bearer $token")
        userEmailFlow.value = user.email
        Result.success(user)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to get profile", e)
        Result.failure(e)
    }

    // User operations (local)
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun resetPassword(email: String, newPassword: String) {
        apiService.resetPassword(ResetPasswordDto(email = email, newPassword = newPassword))
    }

    suspend fun updateProfile(user: User) {
        val token = authToken ?: throw Exception("Not authenticated")
        apiService.updateProfile(
            "Bearer $token",
            UpdateProfileDto(
                username = user.fullName,
                university = user.university,
                faculty = user.faculty,
                studyHours = user.studyHours,
                deansListProgress = user.deansListProgress,
                monthlyIncome = user.monthlyIncome,
                monthlyBudgetLimit = user.monthlyBudgetLimit
            )
        )
    }

    // Tasks operations
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allTasks: Flow<List<Task>> = userEmailFlow.flatMapLatest { email ->
        email?.let { taskDao.getAllTasks(it) } ?: flowOf(emptyList())
    }

    suspend fun syncTasksFromBackend(): Result<Unit> = try {
        val token = authToken ?: throw Exception("Not authenticated")
        val email = userEmailFlow.value ?: throw Exception("User email unknown")
        val remoteTasks = apiService.getTasks("Bearer $token")
        remoteTasks.forEach { remoteTask ->
            val localTask = Task(
                id = remoteTask.id,
                userEmail = email,
                title = remoteTask.title,
                priority = remoteTask.priority,
                isCompleted = remoteTask.isCompleted,
                category = remoteTask.category,
                dueDate = remoteTask.dueDate
            )
            taskDao.insertTask(localTask)
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to sync tasks", e)
        Result.failure(e)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
        try {
            val token = authToken ?: return
            val createTaskDto = CreateTaskDto(
                title = task.title,
                priority = task.priority,
                category = task.category,
                dueDate = task.dueDate
            )
            val remoteTask = apiService.createTask("Bearer $token", createTaskDto)
            val updatedTask = task.copy(id = remoteTask.id)
            taskDao.updateTask(updatedTask)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync task to backend", e)
        }
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
        try {
            val token = authToken ?: return
            val taskDto = TaskDto(
                id = task.id,
                title = task.title,
                priority = task.priority,
                isCompleted = task.isCompleted,
                category = task.category,
                dueDate = task.dueDate
            )
            apiService.updateTask("Bearer $token", task.id, taskDto)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update task on backend", e)
        }
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
        try {
            val token = authToken ?: return
            apiService.deleteTask("Bearer $token", task.id)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete task from backend", e)
        }
    }

    suspend fun clearTasks() {
        userEmailFlow.value?.let { taskDao.clearAll(it) }
    }

    // Classes operations
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allClasses: Flow<List<ClassEvent>> = userEmailFlow.flatMapLatest { email ->
        email?.let { classDao.getAllClasses(it) } ?: flowOf(emptyList())
    }

    suspend fun syncClassesFromBackend(): Result<Unit> = try {
        val token = authToken ?: throw Exception("Not authenticated")
        val email = userEmailFlow.value ?: throw Exception("User email unknown")
        val remoteClasses = apiService.getClasses("Bearer $token")
        remoteClasses.forEach { remoteClass ->
            val localClass = ClassEvent(
                id = remoteClass.id,
                userEmail = email,
                name = remoteClass.name,
                timeRange = remoteClass.timeRange,
                dayOfWeek = remoteClass.dayOfWeek,
                type = remoteClass.type
            )
            classDao.insertClass(localClass)
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to sync classes", e)
        Result.failure(e)
    }

    suspend fun insertClass(classEvent: ClassEvent) {
        classDao.insertClass(classEvent)
        try {
            val token = authToken ?: return
            val createClassDto = CreateClassDto(
                name = classEvent.name,
                timeRange = classEvent.timeRange,
                dayOfWeek = classEvent.dayOfWeek,
                type = classEvent.type
            )
            val remoteClass = apiService.createClass("Bearer $token", createClassDto)
            val updatedClass = classEvent.copy(id = remoteClass.id)
            classDao.updateClass(updatedClass)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync class to backend", e)
        }
    }

    suspend fun deleteClass(classEvent: ClassEvent) {
        classDao.deleteClass(classEvent)
        try {
            val token = authToken ?: return
            apiService.deleteClass("Bearer $token", classEvent.id)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete class from backend", e)
        }
    }

    suspend fun clearClasses() {
        userEmailFlow.value?.let { classDao.clearAll(it) }
    }

    // Exams operations
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allExams: Flow<List<Exam>> = userEmailFlow.flatMapLatest { email ->
        email?.let { examDao.getAllExams(it) } ?: flowOf(emptyList())
    }

    suspend fun insertExam(exam: Exam) {
        examDao.insertExam(exam)
        try {
            val token = authToken ?: return
            Log.d(TAG, "Exam inserted locally for user: ${exam.userEmail}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync exam to backend", e)
        }
    }

    suspend fun updateExam(exam: Exam) {
        examDao.updateExam(exam)
        try {
            val token = authToken ?: return
            Log.d(TAG, "Exam updated locally for user: ${exam.userEmail}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update exam on backend", e)
        }
    }

    suspend fun deleteExam(exam: Exam) {
        examDao.deleteExam(exam)
        try {
            val token = authToken ?: return
            Log.d(TAG, "Exam deleted locally for user: ${exam.userEmail}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete exam from backend", e)
        }
    }

    suspend fun clearExams() {
        userEmailFlow.value?.let { examDao.clearAll(it) }
    }

    // Transactions operations
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allTransactions: Flow<List<FinanceTransaction>> = userEmailFlow.flatMapLatest { email ->
        email?.let { transactionDao.getAllTransactions(it) } ?: flowOf(emptyList())
    }

    suspend fun syncTransactionsFromBackend(): Result<Unit> = try {
        val token = authToken ?: throw Exception("Not authenticated")
        val email = userEmailFlow.value ?: throw Exception("User email unknown")
        val remoteTransactions = apiService.getTransactions("Bearer $token")
        remoteTransactions.forEach { remoteTransaction ->
            val localTransaction = FinanceTransaction(
                id = remoteTransaction.id,
                userEmail = email,
                title = remoteTransaction.title,
                amount = remoteTransaction.amount,
                category = remoteTransaction.category,
                dateText = remoteTransaction.dateText,
                timestamp = remoteTransaction.timestamp
            )
            transactionDao.insertTransaction(localTransaction)
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to sync transactions", e)
        Result.failure(e)
    }

    suspend fun insertTransaction(transaction: FinanceTransaction) {
        transactionDao.insertTransaction(transaction)
        try {
            val token = authToken ?: return
            val createTransactionDto = CreateTransactionDto(
                title = transaction.title,
                amount = transaction.amount,
                category = transaction.category,
                dateText = transaction.dateText
            )
            val remoteTransaction = apiService.createTransaction("Bearer $token", createTransactionDto)
            val updatedTransaction = transaction.copy(id = remoteTransaction.id, timestamp = remoteTransaction.timestamp)
            transactionDao.insertTransaction(updatedTransaction)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync transaction to backend", e)
        }
    }

    suspend fun deleteTransaction(transaction: FinanceTransaction) {
        transactionDao.deleteTransaction(transaction)
        try {
            val token = authToken ?: return
            apiService.deleteTransaction("Bearer $token", transaction.id)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete transaction from backend", e)
        }
    }

    suspend fun clearTransactions() {
        userEmailFlow.value?.let { transactionDao.clearAll(it) }
    }

    // Notes operations
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allNotes: Flow<List<StudyNote>> = userEmailFlow.flatMapLatest { email ->
        email?.let { studyNoteDao.getAllNotes(it) } ?: flowOf(emptyList())
    }

    suspend fun syncNotesFromBackend(): Result<Unit> = try {
        val token = authToken ?: throw Exception("Not authenticated")
        val email = userEmailFlow.value ?: throw Exception("User email unknown")
        val remoteNotes = apiService.getNotes("Bearer $token")
        remoteNotes.forEach { remoteNote ->
            val localNote = StudyNote(
                id = remoteNote.id,
                userEmail = email,
                title = remoteNote.title,
                content = remoteNote.content,
                courseName = remoteNote.courseName,
                dateCreated = remoteNote.dateCreated
            )
            studyNoteDao.insertNote(localNote)
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to sync notes", e)
        Result.failure(e)
    }

    suspend fun insertNote(note: StudyNote) {
        studyNoteDao.insertNote(note)
        try {
            val token = authToken ?: return
            val createNoteDto = CreateNoteDto(
                title = note.title,
                content = note.content,
                courseName = note.courseName
            )
            val remoteNote = apiService.createNote("Bearer $token", createNoteDto)
            val updatedNote = note.copy(id = remoteNote.id, dateCreated = remoteNote.dateCreated)
            studyNoteDao.insertNote(updatedNote)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync note to backend", e)
        }
    }

    suspend fun deleteNote(note: StudyNote) {
        studyNoteDao.deleteNote(note)
        try {
            val token = authToken ?: return
            apiService.deleteNote("Bearer $token", note.id)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete note from backend", e)
        }
    }

    suspend fun clearNotes() {
        userEmailFlow.value?.let { studyNoteDao.clearAll(it) }
    }

    // Grades operations
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allGrades: Flow<List<CourseGrade>> = userEmailFlow.flatMapLatest { email ->
        email?.let { courseGradeDao.getAllGrades(it) } ?: flowOf(emptyList())
    }

    suspend fun syncGradesFromBackend(): Result<Unit> = try {
        val token = authToken ?: throw Exception("Not authenticated")
        val email = userEmailFlow.value ?: throw Exception("User email unknown")
        val remoteGrades = apiService.getGrades("Bearer $token")
        remoteGrades.forEach { remoteGrade ->
            val localGrade = CourseGrade(
                id = remoteGrade.id,
                userEmail = email,
                courseName = remoteGrade.courseName,
                gradeLetter = remoteGrade.gradeLetter,
                creditHours = remoteGrade.creditHours,
                term = remoteGrade.term
            )
            courseGradeDao.insertGrade(localGrade)
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to sync grades", e)
        Result.failure(e)
    }

    suspend fun insertGrade(grade: CourseGrade) {
        courseGradeDao.insertGrade(grade)
        try {
            val token = authToken ?: return
            val createGradeDto = CreateGradeDto(
                courseName = grade.courseName,
                gradeLetter = grade.gradeLetter,
                creditHours = grade.creditHours,
                term = grade.term
            )
            val remoteGrade = apiService.createGrade("Bearer $token", createGradeDto)
            val updatedGrade = grade.copy(id = remoteGrade.id)
            courseGradeDao.insertGrade(updatedGrade)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync grade to backend", e)
        }
    }

    suspend fun deleteGrade(grade: CourseGrade) {
        courseGradeDao.deleteGrade(grade)
        try {
            val token = authToken ?: return
            apiService.deleteGrade("Bearer $token", grade.id)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete grade from backend", e)
        }
    }

    suspend fun clearGrades() {
        userEmailFlow.value?.let { courseGradeDao.clearAll(it) }
    }

    // Resources operations
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allResources: Flow<List<StudyResource>> = userEmailFlow.flatMapLatest { email ->
        email?.let { studyResourceDao.getAllResources(it) } ?: flowOf(emptyList())
    }

    suspend fun syncResourcesFromBackend(): Result<Unit> = try {
        val token = authToken ?: throw Exception("Not authenticated")
        val email = userEmailFlow.value ?: throw Exception("User email unknown")
        val remoteResources = apiService.getResources("Bearer $token")
        remoteResources.forEach { remoteResource ->
            val localResource = StudyResource(
                id = remoteResource.id,
                userEmail = email,
                title = remoteResource.title,
                url = remoteResource.url,
                category = remoteResource.category,
                notes = remoteResource.notes,
                courseName = remoteResource.courseName,
                dateAdded = remoteResource.dateAdded
            )
            studyResourceDao.insertResource(localResource)
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to sync resources", e)
        Result.failure(e)
    }

    suspend fun insertResource(resource: StudyResource) {
        studyResourceDao.insertResource(resource)
        try {
            val token = authToken ?: return
            val createResourceDto = CreateResourceDto(
                title = resource.title,
                url = resource.url,
                category = resource.category,
                notes = resource.notes,
                courseName = resource.courseName
            )
            val remoteResource = apiService.createResource("Bearer $token", createResourceDto)
            val updatedResource = resource.copy(id = remoteResource.id, dateAdded = remoteResource.dateAdded)
            studyResourceDao.insertResource(updatedResource)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync resource to backend", e)
        }
    }

    suspend fun deleteResource(resource: StudyResource) {
        studyResourceDao.deleteResource(resource)
        try {
            val token = authToken ?: return
            apiService.deleteResource("Bearer $token", resource.id)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete resource from backend", e)
        }
    }

    suspend fun clearResources() {
        userEmailFlow.value?.let { studyResourceDao.clearAll(it) }
    }

    // Subjects operations
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allSubjects: Flow<List<Subject>> = userEmailFlow.flatMapLatest { email ->
        email?.let { subjectDao.getAllSubjects(it) } ?: flowOf(emptyList())
    }

    suspend fun syncSubjectsFromBackend(): Result<Unit> = try {
        val token = authToken ?: throw Exception("Not authenticated")
        val email = userEmailFlow.value ?: throw Exception("User email unknown")
        val remoteSubjects = apiService.getSubjects("Bearer $token")
        remoteSubjects.forEach { remoteSubject ->
            val localSubject = Subject(
                id = remoteSubject.id,
                userEmail = email,
                name = remoteSubject.name,
                color = remoteSubject.color,
                icon = remoteSubject.icon ?: "School"
            )
            subjectDao.insertSubject(localSubject)
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to sync subjects", e)
        Result.failure(e)
    }

    suspend fun insertSubject(subject: Subject) {
        subjectDao.insertSubject(subject)
        try {
            val token = authToken ?: return
            val createSubjectDto = CreateSubjectDto(
                name = subject.name,
                color = subject.color,
                icon = subject.icon
            )
            val remoteSubject = apiService.createSubject("Bearer $token", createSubjectDto)
            val updatedSubject = subject.copy(id = remoteSubject.id)
            subjectDao.insertSubject(updatedSubject)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync subject to backend", e)
        }
    }

    suspend fun updateSubject(subject: Subject) {
        subjectDao.updateSubject(subject)
        try {
            val token = authToken ?: return
            val subjectDto = SubjectDto(
                id = subject.id,
                name = subject.name,
                color = subject.color,
                icon = subject.icon
            )
            apiService.updateSubject("Bearer $token", subject.id, subjectDto)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update subject on backend", e)
        }
    }

    suspend fun deleteSubject(subject: Subject) {
        subjectDao.deleteSubject(subject)
        try {
            val token = authToken ?: return
            apiService.deleteSubject("Bearer $token", subject.id)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete subject from backend", e)
        }
    }

    suspend fun clearSubjects() {
        userEmailFlow.value?.let { subjectDao.clearAll(it) }
    }
}
