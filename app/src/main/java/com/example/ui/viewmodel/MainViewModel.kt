package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.*
import com.example.data.local.*
import com.example.data.repository.SyncedStudyRepository
import com.example.notifications.NotificationHelper
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import okhttp3.MediaType.Companion.toMediaType

enum class AppScreen {
    Splash,
    OnboardingStage1,
    OnboardingStage2,
    OnboardingStage3,
    OnboardingStage4,
    Login,
    SignUp,
    ForgotPassword,
    SubjectManagement,
    Main
}

enum class MainTab {
    Dashboard,
    Tutor,
    Schedule,
    Finances,
    Profile
}

enum class ChatMode {
    General,
    ExplainLecture,
    Quiz,
    MentalHealth
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val time: String,
    val attachedFile: String? = null,
    val attachedFileName: String? = null,
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("lifeu_prefs", Context.MODE_PRIVATE)

    private val database = AppDatabase.getDatabase(application)
    private val repository = SyncedStudyRepository(
        database.taskDao(),
        database.classDao(),
        database.transactionDao(),
        database.studyNoteDao(),
        database.courseGradeDao(),
        database.studyResourceDao(),
        database.userDao(),
        database.examDao(),
        database.subjectDao(),
        StudyAppRetrofitClient.service
    )

    // Screen State Navigation
    var currentScreen by mutableStateOf(AppScreen.Splash)
        private set

    var currentTab by mutableStateOf(MainTab.Dashboard)
        private set

    // User Profile Information
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUserFlow: StateFlow<User?> = _currentUser.asStateFlow()
    var currentUser by mutableStateOf<User?>(null)
        private set

    var userName by mutableStateOf("")
    var userUniversity by mutableStateOf("")
    var userFaculty by mutableStateOf("")
    var userAvatarUrl by mutableStateOf("")

    var authError by mutableStateOf<String?>(null)
    var isAuthenticating by mutableStateOf(false)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isAuthenticating = true
            authError = null
            try {
                val response = repository.login(email, password)
                val actualEmail = response.email
                
                // Try to get full user profile from backend
                val profileResult = repository.getProfile()
                val user = if (profileResult.isSuccess) {
                    val dto = profileResult.getOrThrow()
                    var localUser = repository.getUserByEmail(actualEmail)
                    if (localUser == null) {
                        localUser = User(
                            email = actualEmail,
                            password = password,
                            fullName = dto.username,
                            university = dto.university,
                            faculty = dto.faculty
                        )
                        repository.insertUser(localUser)
                    }
                    localUser
                } else {
                    repository.getUserByEmail(actualEmail)
                }

                if (user != null) {
                    currentUser = user
                    _currentUser.value = user
                    userName = user.fullName
                    userUniversity = user.university
                    userFaculty = user.faculty
                    userAvatarUrl = user.avatarUrl
                    userStudyHours = user.studyHours.toString()
                    deansListProgress = user.deansListProgress
                    monthlyIncome = user.monthlyIncome
                    monthlyBudgetLimit = user.monthlyBudgetLimit
                    
                    persistAuthState(repository.getAuthToken() ?: "", actualEmail)
                    
                    // Trigger sync
                    syncData()
                    
                    navigateTo(AppScreen.Main)
                } else {
                    authError = "User not found locally after login"
                }
            } catch (e: Exception) {
                authError = "Login failed: ${SyncedStudyRepository.errorMessage(e)}"
            }
            isAuthenticating = false
        }
    }

    fun signUp(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            isAuthenticating = true
            authError = null
            try {
                val response = repository.register(email, password, fullName)
                repository.setAuthToken(response.token, email)
                
                val newUser = User(
                    email = email,
                    password = password,
                    fullName = fullName,
                    university = userUniversity,
                    faculty = userFaculty
                )
                repository.insertUser(newUser)
                
                currentUser = newUser
                _currentUser.value = newUser
                userName = fullName
                userStudyHours = "0"
                deansListProgress = 0f
                monthlyIncome = 0.0
                monthlyBudgetLimit = 0.0

                persistAuthState(response.token, email)

                // Sync the initial profile (including Uni/Faculty) to the server
                try {
                    repository.updateProfile(newUser)
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Initial profile sync failed during signup", e)
                }
                
                navigateTo(AppScreen.Main)
            } catch (e: Exception) {
                authError = "Sign up failed: ${SyncedStudyRepository.errorMessage(e)}"
            }
            isAuthenticating = false
        }
    }

    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            isAuthenticating = true
            authError = null
            try {
                val credentialManager = CredentialManager.create(context)
                
                // 1. First attempt: Try to find an already authorized account for a seamless experience
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(GOOGLE_WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(true)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = try {
                    credentialManager.getCredential(context, request)
                } catch (e: Exception) {
                    // 2. Fallback: If no authorized account is found, use GetSignInWithGoogleOption
                    // This is a more explicit "Button Flow" that forces the account picker to show.
                    if (e is NoCredentialException) {
                        Log.d("MainViewModel", "No authorized account found, switching to GetSignInWithGoogleOption")
                        
                        val fallbackOption = GetSignInWithGoogleOption.Builder(GOOGLE_WEB_CLIENT_ID)
                            .build()
                        
                        val fallbackRequest = GetCredentialRequest.Builder()
                            .addCredentialOption(fallbackOption)
                            .build()
                        
                        credentialManager.getCredential(context, fallbackRequest)
                    } else {
                        throw e
                    }
                }

                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val email = googleIdTokenCredential.id
                val displayName = googleIdTokenCredential.displayName ?: email

                // Send ID token to backend
                val response = repository.googleLogin(idToken, email, displayName)
                repository.setAuthToken(response.token, response.email)

                val userEmail = response.email
                var localUser = repository.getUserByEmail(userEmail)
                if (localUser == null) {
                    localUser = User(
                        email = userEmail,
                        password = "",
                        fullName = response.username
                    )
                    repository.insertUser(localUser)
                }

                currentUser = localUser
                _currentUser.value = localUser
                userName = localUser.fullName
                userUniversity = localUser.university
                userFaculty = localUser.faculty
                userAvatarUrl = localUser.avatarUrl
                userStudyHours = localUser.studyHours.toString()
                deansListProgress = localUser.deansListProgress
                monthlyIncome = localUser.monthlyIncome
                monthlyBudgetLimit = localUser.monthlyBudgetLimit

                persistAuthState(response.token, userEmail)

                syncData()
                navigateTo(AppScreen.Main)
            } catch (_: GetCredentialCancellationException) {
                Log.d("MainViewModel", "Google sign-in cancelled by user")
                authError = null
            } catch (e: GetCredentialException) {
                Log.e("MainViewModel", "Google sign-in failed (GetCredentialException): ${e.type}", e)
                authError = "Google sign-in failed: ${SyncedStudyRepository.errorMessage(e)} (Type: ${e.type})"
            } catch (e: Exception) {
                Log.e("MainViewModel", "Google sign-in failed (General Exception)", e)
                authError = "Google sign-in failed: ${e.localizedMessage ?: "Unknown error"}"
            }
            isAuthenticating = false
        }
    }

    companion object {
        val GOOGLE_WEB_CLIENT_ID: String = BuildConfig.GOOGLE_WEB_CLIENT_ID
    }

    // Forgot Password / OTP State
    var forgotPasswordEmail by mutableStateOf("")
    var generatedOtp by mutableStateOf("")
        private set
    var isOtpSent by mutableStateOf(false)
        private set
    var isOtpVerified by mutableStateOf(false)
        private set
    var forgotPasswordError by mutableStateOf<String?>(null)
    var forgotPasswordSuccess by mutableStateOf<String?>(null)
    var isSendingOtp by mutableStateOf(false)
        private set

    fun sendOtpToEmail(email: String) {
        viewModelScope.launch {
            isSendingOtp = true
            forgotPasswordError = null
            forgotPasswordSuccess = null
            try {
                val otp = if (generatedOtp.isNotEmpty()) generatedOtp else (100000..999999).random().toString()
                generatedOtp = otp
                forgotPasswordEmail = email

                val serviceId = BuildConfig.EMAILJS_SERVICE_ID
                val templateId = BuildConfig.EMAILJS_TEMPLATE_ID
                val publicKey = BuildConfig.EMAILJS_PUBLIC_KEY

                if (serviceId.startsWith("YOUR_") || templateId.startsWith("YOUR_") || publicKey.startsWith("YOUR_")) {
                    forgotPasswordError = "EmailJS is not configured. Please set EMAILJS_SERVICE_ID, EMAILJS_TEMPLATE_ID, and EMAILJS_PUBLIC_KEY in your .env file."
                    isSendingOtp = false
                    return@launch
                }

                val json = org.json.JSONObject().apply {
                    put("service_id", serviceId)
                    put("template_id", templateId)
                    put("user_id", publicKey)
                    put("template_params", org.json.JSONObject().apply {
                        put("to_email", email)
                        put("email", email)
                        put("reply_to", email)
                        put("from_name", "Life U")
                        put("otp_code", otp)
                        put("to_name", email.substringBefore("@"))
                        put("message", "Your verification code is: $otp")
                    })
                }

                val client = okhttp3.OkHttpClient.Builder()
                    .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
                val body = json.toString()
                    .toByteArray()
                    .let { bytes ->
                        okhttp3.RequestBody.create(
                            "application/json".toMediaType(),
                            bytes
                        )
                    }
                val request = okhttp3.Request.Builder()
                    .url("https://api.emailjs.com/api/v1.0/email/send")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                val response = kotlinx.coroutines.withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                val responseBody = response.body?.string() ?: ""
                Log.d("MainViewModel", "EmailJS response: ${response.code} - $responseBody")

                if (response.isSuccessful) {
                    isOtpSent = true
                    forgotPasswordSuccess = "OTP sent to $email"
                } else {
                    forgotPasswordError = "EmailJS error (${response.code}): $responseBody"
                }
            } catch (e: Exception) {
                forgotPasswordError = "Failed to send OTP: ${SyncedStudyRepository.errorMessage(e)}"
            }
            isSendingOtp = false
        }
    }

    fun verifyOtp(enteredOtp: String): Boolean {
        return if (enteredOtp == generatedOtp) {
            isOtpVerified = true
            forgotPasswordError = null
            true
        } else {
            forgotPasswordError = "Invalid OTP. Please try again."
            false
        }
    }

    fun resetPassword(newPassword: String) {
        viewModelScope.launch {
            isSendingOtp = true
            forgotPasswordError = null
            try {
                // Update password in local database and backend
                val user = repository.getUserByEmail(forgotPasswordEmail)
                if (user != null) {
                    val updatedUser = user.copy(password = newPassword)
                    repository.updateUser(updatedUser)
                }
                try {
                    repository.resetPassword(forgotPasswordEmail, newPassword)
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Failed to sync password reset to backend", e)
                }
                forgotPasswordSuccess = "Password reset successfully!"
                // Reset state after short delay, but keep email for login screen
                delay(1500)
                val resetEmail = forgotPasswordEmail
                resetForgotPasswordState()
                // Set the email back so LoginScreen can pick it up
                forgotPasswordEmail = resetEmail
                navigateTo(AppScreen.Login)
            } catch (e: Exception) {
                forgotPasswordError = "Failed to reset password: ${SyncedStudyRepository.errorMessage(e)}"
            }
            isSendingOtp = false
        }
    }

    fun resetForgotPasswordState() {
        forgotPasswordEmail = ""
        generatedOtp = ""
        isOtpSent = false
        isOtpVerified = false
        forgotPasswordError = null
        forgotPasswordSuccess = null
        isSendingOtp = false
    }

    private fun syncData() {
        viewModelScope.launch {
            repository.syncTasksFromBackend()
            repository.syncClassesFromBackend()
            repository.syncNotesFromBackend()
            repository.syncGradesFromBackend()
            repository.syncResourcesFromBackend()
            repository.syncTransactionsFromBackend()
            repository.syncSubjectsFromBackend()
        }
    }

    fun updateProfile(name: String, university: String, faculty: String) {
        userName = name
        userUniversity = university
        userFaculty = faculty
        currentUser?.let { user ->
            val updatedUser = user.copy(fullName = name, university = university, faculty = faculty, avatarUrl = userAvatarUrl)
            currentUser = updatedUser
            _currentUser.value = updatedUser
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateUser(updatedUser)
                try {
                    repository.updateProfile(updatedUser)
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Failed to sync profile update to backend", e)
                }
            }
        }
    }

    fun updateAvatar(uri: String) {
        userAvatarUrl = uri
        currentUser?.let { user ->
            val updatedUser = user.copy(avatarUrl = uri)
            currentUser = updatedUser
            _currentUser.value = updatedUser
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateUser(updatedUser)
                try {
                    repository.updateProfile(updatedUser)
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Failed to sync avatar update to backend", e)
                }
            }
        }
    }

    fun updateBudgetSettings(income: Double, limit: Double) {
        monthlyIncome = income
        monthlyBudgetLimit = limit
        currentUser?.let { user ->
            val updatedUser = user.copy(monthlyIncome = income, monthlyBudgetLimit = limit)
            currentUser = updatedUser
            _currentUser.value = updatedUser
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateUser(updatedUser)
            }
        }
    }
    
    fun getGradePoints(letter: String): Double {
        return when (letter.trim().uppercase()) {
            "A+", "A" -> 4.0
            "A-" -> 3.7
            "B+" -> 3.3
            "B" -> 3.0
            "B-" -> 2.7
            "C+" -> 2.3
            "C" -> 2.0
            "C-" -> 1.7
            "D+" -> 1.3
            "D" -> 1.0
            "F" -> 0.0
            else -> 4.0
        }
    }

    val userGpa: String
        get() {
            val gradeList = grades.value
            if (gradeList.isEmpty()) return "0.00"
            var totalPoints = 0.0
            var totalCredits = 0
            for (g in gradeList) {
                totalPoints += getGradePoints(g.gradeLetter) * g.creditHours
                totalCredits += g.creditHours
            }
            return if (totalCredits > 0) {
                String.format(Locale.US, "%.2f", totalPoints / totalCredits)
            } else {
                "0.00"
            }
        }

    var userStudyHours by mutableStateOf("0")
    var deansListProgress by mutableFloatStateOf(0.0f)
    var isDarkTheme by mutableStateOf(false)
    
    // Budget & Finances State
    var monthlyIncome by mutableDoubleStateOf(2500.00)
    var monthlyBudgetLimit by mutableDoubleStateOf(1500.00)
    var showEditBudgetDialog by mutableStateOf(false)

    // Enhanced Pomodoro Study Timer State
    enum class PomodoroMode {
        WORK, SHORT_BREAK, LONG_BREAK
    }

    data class FocusSession(
        val id: String = UUID.randomUUID().toString(),
        val subject: String,
        val durationMinutes: Int,
        val mode: PomodoroMode,
        val timestamp: String
    )

    var selectedStudySubject by mutableStateOf("Advanced Biology")
    var pomodoroMode by mutableStateOf(PomodoroMode.WORK)
    
    var workDurationMinutes by mutableIntStateOf(25)
    var shortBreakMinutes by mutableIntStateOf(5)
    var longBreakMinutes by mutableIntStateOf(15)

    var showSessionCompletedDialog by mutableStateOf(false)
    var lastCompletedSessionType by mutableStateOf("")

    var focusHistory by mutableStateOf<List<FocusSession>>(emptyList())

    var focusTimerLeftSeconds by mutableIntStateOf(25 * 60)
    var isTimerRunning by mutableStateOf(false)
    private var timerJob: Job? = null

    // Room Database State Flows
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var dismissedUrgentTaskIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    fun dismissUrgentTask(taskId: Int) {
        dismissedUrgentTaskIds = dismissedUrgentTaskIds + taskId
    }

    fun isDueWithin24Hours(dueDateStr: String): Boolean {
        val trimmed = dueDateStr.trim().lowercase()
        if (trimmed.isEmpty()) return false
        if (trimmed == "today" || trimmed == "24 hours" || trimmed == "24h" || trimmed == "now") {
            return true
        }
        
        try {
            val sdf = SimpleDateFormat("MMM d", Locale.US)
            val parsedDate = sdf.parse(dueDateStr) ?: return false
            
            val now = Calendar.getInstance()
            val taskCal = Calendar.getInstance().apply { time = parsedDate }
            
            val currentMonth = now.get(Calendar.MONTH)
            val currentDay = now.get(Calendar.DAY_OF_MONTH)
            
            val taskMonth = taskCal.get(Calendar.MONTH)
            val taskDay = taskCal.get(Calendar.DAY_OF_MONTH)
            
            if (currentMonth == taskMonth && currentDay == taskDay) {
                return true
            }
            
            val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
            val tomorrowMonth = tomorrow.get(Calendar.MONTH)
            val tomorrowDay = tomorrow.get(Calendar.DAY_OF_MONTH)
            
            if (tomorrowMonth == taskMonth && tomorrowDay == taskDay) {
                return true
            }
        } catch (e: Exception) {
            // ignore
        }
        return false
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val urgentTasks: StateFlow<List<Task>> = tasks
        .map { list -> list.filter { !it.isCompleted && isDueWithin24Hours(it.dueDate) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val classes: StateFlow<List<ClassEvent>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val transactions: StateFlow<List<FinanceTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val notes: StateFlow<List<StudyNote>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val grades: StateFlow<List<CourseGrade>> = repository.allGrades
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val resources: StateFlow<List<StudyResource>> = repository.allResources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val exams: StateFlow<List<Exam>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val subjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjectNames: StateFlow<List<String>> = subjects.map { list ->
        list.map { it.name }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Feedback & Dialog states
    var showAddTaskDialog by mutableStateOf(false)
    var showAddClassDialog by mutableStateOf(false)
    var showAddExamDialog by mutableStateOf(false)
    var showAddTransactionDialog by mutableStateOf(false)
    var showAddNoteDialog by mutableStateOf(false)
    var showAddGradeDialog by mutableStateOf(false)
    var showAddResourceDialog by mutableStateOf(false)

    var selectedNoteContext by mutableStateOf<StudyNote?>(null)

    fun addNote(title: String, content: String, courseName: String) {
        val userEmail = currentUser?.email ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val dateStr = SimpleDateFormat("MMM d", Locale.US).format(Date())
            repository.insertNote(StudyNote(userEmail = userEmail, title = title, content = content, courseName = courseName, dateCreated = dateStr))
        }
    }

    fun addResource(title: String, url: String, category: String, notes: String = "", courseName: String = "") {
        val userEmail = currentUser?.email ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date())
            repository.insertResource(
                StudyResource(
                    userEmail = userEmail,
                    title = title,
                    url = url,
                    category = category,
                    notes = notes,
                    courseName = courseName,
                    dateAdded = dateStr
                )
            )
        }
    }

    fun deleteResource(resource: StudyResource) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteResource(resource)
        }
    }

    fun deleteNote(note: StudyNote) {
        if (selectedNoteContext?.id == note.id) {
            selectedNoteContext = null
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
        }
    }

    fun addGrade(courseName: String, gradeLetter: String, creditHours: Int, term: String) {
        val userEmail = currentUser?.email ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertGrade(CourseGrade(userEmail = userEmail, courseName = courseName, gradeLetter = gradeLetter, creditHours = creditHours, term = term))
        }
    }

    fun deleteGrade(grade: CourseGrade) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteGrade(grade)
        }
    }

    fun clearGrades() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearGrades()
        }
    }

    // Subjects Actions
    var showAddSubjectDialog by mutableStateOf(false)

    fun addSubject(name: String, color: String = "#3B82F6", icon: String = "School") {
        val userEmail = currentUser?.email ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSubject(Subject(userEmail = userEmail, name = name, color = color, icon = icon))
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSubject(subject)
        }
    }

    // U AI Chat States
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    var isUThinking by mutableStateOf(false)
        private set

    var chatMode by mutableStateOf(ChatMode.General)
    var isListening by mutableStateOf(false)
    var isSpeaking by mutableStateOf(false)
    var autoSpeak by mutableStateOf(false)

    // Attached file state
    var attachedFileUri by mutableStateOf<Uri?>(null)
        private set
    var attachedFileName by mutableStateOf<String?>(null)
        private set
    var attachedFileMimeType by mutableStateOf<String?>(null)
        private set
    var isFileReady by mutableStateOf(false)
        private set
    private var attachedFileBase64: String? = null
    private var fileReadDeferred = CompletableDeferred<Boolean>()

    // Text-to-Speech
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    init {
        NotificationHelper.createNotificationChannels(application)
        initTts()
        restorePersistedState()
    }

    private fun restorePersistedState() {
        val onboardingDone = prefs.getBoolean("onboarding_completed", false)
        val savedToken = prefs.getString("auth_token", null)
        val savedEmail = prefs.getString("auth_email", null)

        if (onboardingDone && savedToken != null && savedEmail != null) {
            // Restore auth state and go directly to Main
            repository.setAuthToken(savedToken, savedEmail)
            viewModelScope.launch {
                val user = repository.getUserByEmail(savedEmail)
                if (user != null) {
                    currentUser = user
                    _currentUser.value = user
                    userName = user.fullName
                    userUniversity = user.university
                    userFaculty = user.faculty
                    userAvatarUrl = user.avatarUrl
                    userStudyHours = user.studyHours.toString()
                    deansListProgress = user.deansListProgress
                    monthlyIncome = user.monthlyIncome
                    monthlyBudgetLimit = user.monthlyBudgetLimit
                    syncData()
                }
                navigateTo(AppScreen.Main)
            }
        } else if (onboardingDone) {
            navigateTo(AppScreen.Login)
        }
        // If onboarding not done, stay on Splash (SplashScreen will handle the delay then navigate)
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean("onboarding_completed", true).apply()
    }

    private fun persistAuthState(token: String, email: String) {
        prefs.edit()
            .putString("auth_token", token)
            .putString("auth_email", email)
            .apply()
    }

    fun logout() {
        prefs.edit()
            .remove("auth_token")
            .remove("auth_email")
            .apply()
        repository.setAuthToken("", null)
        currentUser = null
        _currentUser.value = null
        navigateTo(AppScreen.Login)
    }

    private fun initTts() {
        tts = TextToSpeech(getApplication()) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
            if (ttsReady) {
                tts?.language = Locale.US
            }
        }
    }

    fun speakText(text: String) {
        if (!ttsReady) return
        isSpeaking = true
        val cleanText = text
            .replace(Regex("[*#_`~>|]"), "")
            .replace(Regex("\\[.*?]\\(.*?\\)"), "")
            .take(3500)
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
        tts?.setOnUtteranceCompletedListener { isSpeaking = false }
    }

    fun stopSpeaking() {
        tts?.stop()
        isSpeaking = false
    }

    fun attachFile(uri: Uri, fileName: String, mimeType: String?) {
        attachedFileUri = uri
        attachedFileName = fileName
        attachedFileMimeType = mimeType ?: "application/octet-stream"
        isFileReady = false
        fileReadDeferred = CompletableDeferred()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                if (bytes != null) {
                    attachedFileBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                }
                isFileReady = true
                fileReadDeferred.complete(true)
            } catch (_: Exception) {
                attachedFileBase64 = null
                isFileReady = true
                fileReadDeferred.complete(false)
            }
        }
    }

    fun clearAttachedFile() {
        attachedFileUri = null
        attachedFileName = null
        attachedFileMimeType = null
        attachedFileBase64 = null
        isFileReady = false
        fileReadDeferred = CompletableDeferred()
    }

    // Notification helpers
    fun sendTaskDeadlineNotification(taskTitle: String) {
        NotificationHelper.showNotification(
            getApplication(),
            NotificationHelper.CHANNEL_TASK_DEADLINES,
            "⏰ Deadline Approaching!",
            "\"$taskTitle\" is due soon. Stay on track!"
        )
    }

    fun sendPomodoroNotification(sessionType: String) {
        NotificationHelper.showNotification(
            getApplication(),
            NotificationHelper.CHANNEL_POMODORO,
            "🍅 $sessionType Complete!",
            if (sessionType.contains("Focus")) "Great work! Time for a break." else "Break's over! Ready to focus again?"
        )
    }

    fun scheduleWellnessReminder() {
        NotificationHelper.scheduleWellnessCheckIn(getApplication())
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
    }

    // Navigation Methods
    fun navigateTo(screen: AppScreen) {
        authError = null
        currentScreen = screen
    }

    fun selectTab(tab: MainTab) {
        currentTab = tab
    }

    // Timer Methods
    fun changePomodoroMode(mode: PomodoroMode) {
        pauseFocusTimer()
        pomodoroMode = mode
        focusTimerLeftSeconds = when (mode) {
            PomodoroMode.WORK -> workDurationMinutes * 60
            PomodoroMode.SHORT_BREAK -> shortBreakMinutes * 60
            PomodoroMode.LONG_BREAK -> longBreakMinutes * 60
        }
    }

    fun updateDurations(work: Int, short: Int, long: Int) {
        workDurationMinutes = work
        shortBreakMinutes = short
        longBreakMinutes = long
        if (!isTimerRunning) {
            focusTimerLeftSeconds = when (pomodoroMode) {
                PomodoroMode.WORK -> workDurationMinutes * 60
                PomodoroMode.SHORT_BREAK -> shortBreakMinutes * 60
                PomodoroMode.LONG_BREAK -> longBreakMinutes * 60
            }
        }
    }

    fun startFocusTimer() {
        if (isTimerRunning) return
        isTimerRunning = true
        timerJob = viewModelScope.launch {
            while (focusTimerLeftSeconds > 0 && isTimerRunning) {
                delay(1000)
                focusTimerLeftSeconds--
            }
            if (focusTimerLeftSeconds == 0) {
                isTimerRunning = false
                if (pomodoroMode == PomodoroMode.WORK) {
                    val currentHours = userStudyHours.toIntOrNull() ?: 0
                    val newHours = currentHours + 1
                    userStudyHours = newHours.toString()
                    
                    currentUser?.let { user ->
                        val updatedUser = user.copy(studyHours = newHours)
                        currentUser = updatedUser
                        _currentUser.value = updatedUser
                        viewModelScope.launch(Dispatchers.IO) {
                            repository.updateUser(updatedUser)
                        }
                    }
                    
                    val sdf = SimpleDateFormat("hh:mm a", Locale.US)
                    val formattedTime = sdf.format(Date())
                    focusHistory = listOf(
                        FocusSession(
                            subject = selectedStudySubject,
                            durationMinutes = workDurationMinutes,
                            mode = PomodoroMode.WORK,
                            timestamp = "Today • $formattedTime"
                        )
                    ) + focusHistory

                    lastCompletedSessionType = "Focus Session ($selectedStudySubject)"
                    showSessionCompletedDialog = true
                    sendPomodoroNotification("Focus Session")
                    
                    // Auto-advance to short break
                    pomodoroMode = PomodoroMode.SHORT_BREAK
                    focusTimerLeftSeconds = shortBreakMinutes * 60
                } else {
                    lastCompletedSessionType = if (pomodoroMode == PomodoroMode.SHORT_BREAK) "Short Break" else "Long Break"
                    showSessionCompletedDialog = true
                    sendPomodoroNotification(lastCompletedSessionType)
                    
                    // Auto-advance to work
                    pomodoroMode = PomodoroMode.WORK
                    focusTimerLeftSeconds = workDurationMinutes * 60
                }
            }
        }
    }

    fun pauseFocusTimer() {
        isTimerRunning = false
        timerJob?.cancel()
    }

    fun resetFocusTimer() {
        isTimerRunning = false
        timerJob?.cancel()
        focusTimerLeftSeconds = when (pomodoroMode) {
            PomodoroMode.WORK -> workDurationMinutes * 60
            PomodoroMode.SHORT_BREAK -> shortBreakMinutes * 60
            PomodoroMode.LONG_BREAK -> longBreakMinutes * 60
        }
    }

    // Database Actions
    fun addTask(title: String, priority: String, category: String, dueDate: String = "") {
        val userEmail = currentUser?.email ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(Task(userEmail = userEmail, title = title, priority = priority, isCompleted = false, category = category, dueDate = dueDate))
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun addClass(name: String, timeRange: String, dayOfWeek: String, type: String) {
        val userEmail = currentUser?.email ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertClass(ClassEvent(userEmail = userEmail, name = name, timeRange = timeRange, dayOfWeek = dayOfWeek, type = type))
        }
    }

    fun addExam(title: String, courseName: String, examDate: String, examTime: String = "", location: String = "", notes: String = "") {
        val userEmail = currentUser?.email ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertExam(
                Exam(
                    userEmail = userEmail,
                    title = title,
                    courseName = courseName,
                    examDate = examDate,
                    examTime = examTime,
                    location = location,
                    notes = notes
                )
            )
        }
    }

    fun updateExam(exam: Exam) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateExam(exam)
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteExam(exam)
        }
    }

    fun addTransaction(title: String, amount: Double, category: String) {
        val userEmail = currentUser?.email ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val isIncome = category == "Income"
            val signedAmount = if (isIncome) abs(amount) else -abs(amount)
            val sdf = SimpleDateFormat("MMM dd • hh:mm a", Locale.getDefault())
            val dateText = sdf.format(Date())
            repository.insertTransaction(FinanceTransaction(userEmail = userEmail, title = title, amount = signedAmount, category = category, dateText = dateText))
        }
    }

    fun deleteTransaction(transaction: FinanceTransaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(transaction)
        }
    }

    // Chat Actions
    fun sendMessageToU(text: String, attachedFile: String? = null, attachedFileName: String? = null) {
        if (text.trim().isEmpty() && attachedFile == null && attachedFileBase64 == null) return

        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeNow = sdf.format(Date())

        val userMsg = ChatMessage(
            text = text,
            isUser = true,
            time = timeNow,
            attachedFile = attachedFile,
            attachedFileName = this.attachedFileName
        )
        _chatMessages.value += userMsg

        isUThinking = true

        viewModelScope.launch {
            // Wait for file to finish reading if one is attached
            if (attachedFileUri != null) {
                fileReadDeferred.await()
            }

            // Capture file data after it's ready, then clear
            val fileBase64 = attachedFileBase64
            val fileMime = attachedFileMimeType
            val fileName = attachedFileName
            clearAttachedFile()

            val apiKey = BuildConfig.GROQ_API_KEY
            if (apiKey.isEmpty() || apiKey.startsWith("GROQ_API_KEY")) {
                delay(1500)
                val responseText = "Hi! I would love to answer that, but my Groq API Key is not configured yet. " +
                        "Please set your `GROQ_API_KEY` in the `.env` file. " +
                        "Get a free key at https://console.groq.com/keys"
                val uMsg = ChatMessage(text = responseText, isUser = false, time = sdf.format(Date()))
                _chatMessages.value = _chatMessages.value + uMsg
                isUThinking = false
                return@launch
            }

            // Build conversation history for Groq
            val groqMessages = mutableListOf<GroqConversationMessage>()
            for (msg in _chatMessages.value.takeLast(10)) {
                groqMessages.add(GroqConversationMessage(
                    role = if (msg.isUser) "user" else "assistant",
                    textContent = msg.text
                ))
            }

            // If file is attached, use content parts with image_url
            if (fileBase64 != null && fileMime != null) {
                val parts = mutableListOf<GroqContentPart>()
                val mimeForUrl = fileMime.ifBlank { "image/png" }
                parts.add(GroqContentPart(
                    type = "image_url",
                    imageUrl = "data:$mimeForUrl;base64,$fileBase64"
                ))
                parts.add(GroqContentPart(
                    type = "text",
                    text = text.ifBlank {
                        "I've attached a file${if (fileName != null) " ($fileName)" else ""}. Please analyze it."
                    }
                ))
                // Replace last user message with multi-part version
                if (groqMessages.isNotEmpty() && groqMessages.last().role == "user") {
                    groqMessages.removeLast()
                }
                groqMessages.add(GroqConversationMessage(role = "user", contentParts = parts))
            }

            val systemPrompt = buildSystemPrompt(fileBase64 = fileBase64, fileName = fileName, fileMime = fileMime)

            var lastError: String? = null
            var succeeded = false

            val models = if (fileBase64 != null) GroqClient.allModels() else GroqClient.textModels

            for (model in models) {
                if (succeeded) break
                try {
                    val result = GroqClient.sendMessage(
                        apiKey = apiKey,
                        model = model,
                        messages = groqMessages,
                        systemPrompt = systemPrompt,
                        temperature = if (chatMode == ChatMode.MentalHealth) 0.9f else 0.7f,
                        maxTokens = if (chatMode == ChatMode.Quiz) 2048 else 1024
                    )

                    result.fold(
                        onSuccess = { responseText ->
                            val uMsg = ChatMessage(
                                text = responseText,
                                isUser = false,
                                time = sdf.format(Date())
                            )
                            _chatMessages.value = _chatMessages.value + uMsg
                            if (autoSpeak) speakText(responseText)
                            succeeded = true
                        },
                        onFailure = { e ->
                            val msg = e.localizedMessage ?: "Unknown error"
                            Log.e("GROQ", "Model $model failed: $msg")
                            lastError = msg
                        }
                    )
                } catch (e: Exception) {
                    Log.e("GROQ", "Model $model threw: ${e.localizedMessage}")
                    lastError = e.localizedMessage ?: "Unknown error"
                }
            }

            if (!succeeded) {
                val rawError = lastError ?: "Unknown error"
                val friendlyError = when {
                    rawError.contains("not support image", ignoreCase = true) ||
                    rawError.contains("vision", ignoreCase = true) ->
                        "This file type isn't supported by the current AI model. The file may not be an image, or the model doesn't support vision."
                    rawError.contains("API key", ignoreCase = true) ||
                    rawError.contains("api_key", ignoreCase = true) ||
                    rawError.contains("authorization", ignoreCase = true) ||
                    rawError.contains("unauthorized", ignoreCase = true) ->
                        "There's an issue with the API key. Please check your Groq API key configuration."
                    rawError.contains("quota", ignoreCase = true) ||
                    rawError.contains("rate limit", ignoreCase = true) ||
                    rawError.contains("429", ignoreCase = false) ||
                    rawError.contains("too many requests", ignoreCase = true) ->
                        "The AI is a bit overloaded right now. Please wait a moment and try again."
                    else -> "I ran into an issue: $rawError"
                }
                val uMsg = ChatMessage(
                    text = friendlyError,
                    isUser = false,
                    time = sdf.format(Date())
                )
                _chatMessages.value = _chatMessages.value + uMsg
            }

            isUThinking = false
        }
    }

    fun buildSystemPrompt(fileBase64: String? = null, fileName: String? = null, fileMime: String? = null): String {
        val base = StringBuilder()

        when (chatMode) {
            ChatMode.General -> {
                base.append("You are U, a friendly, supportive, and intelligent university study companion for Life U. ")
                base.append("Help the student with their courses, summarize lessons, generate flashcards/quizzes, and explain concepts simply. ")
                base.append("Always sound approachable, clear, and encouraging. Use elegant Markdown bullet points or clear tables to make learning visual and easy.")
                base.append("\n\n### SPECIAL INSTRUCTIONS: ACTION TAGS\n")
                base.append("If the conversation suggests a task or an event, you MUST append a hidden action tag at the end of your response in this EXACT format:\n")
                base.append("[ACTION: ADD_TASK Title: {name}, Due: {date}, Category: {subject}]\n")
                base.append("[ACTION: ADD_CLASS Name: {name}, Time: {range}, Day: {day}]\n")
                base.append("Example: If you suggest finishing an assignment, add: [ACTION: ADD_TASK Title: Finish Essay, Due: Tomorrow, Category: English]")
            }
            ChatMode.ExplainLecture -> {
                base.append("You are U, an expert lecture explainer for Life U. ")
                base.append("The student will share lecture content, slides, PDFs, or topics. Your job is to:\n")
                base.append("- Break down complex concepts into simple, digestible explanations\n")
                base.append("- Use analogies and real-world examples\n")
                base.append("- Highlight key terms and definitions\n")
                base.append("- Create structured summaries with headers and bullet points\n")
                base.append("- If a file/document is attached, thoroughly analyze and explain its contents\n")
                base.append("Be patient, thorough, and encouraging. Use Markdown formatting.")
            }
            ChatMode.Quiz -> {
                base.append("You are U, a quiz and exam generator for Life U. ")
                base.append("Generate quizzes and practice exams based on the student's study materials. Follow these rules:\n")
                base.append("- Create multiple choice questions (A, B, C, D) with clear correct answers\n")
                base.append("- Include a mix of easy, medium, and hard questions\n")
                base.append("- After the student answers, provide detailed explanations for each answer\n")
                base.append("- If the student says 'generate quiz' or 'make exam', create 5-10 questions from the context\n")
                base.append("- Track score and give encouraging feedback\n")
                base.append("- If a file is attached, generate questions based on its content\n")
                base.append("Use clear Markdown formatting with numbered questions.")
            }
            ChatMode.MentalHealth -> {
                base.append("You are U, a compassionate wellness companion for Life U students. ")
                base.append("The student may be feeling burned out, stressed, anxious, or overwhelmed. Your role is to:\n")
                base.append("- Listen empathetically and validate their feelings\n")
                base.append("- Offer practical stress-relief techniques (breathing exercises, grounding, etc.)\n")
                base.append("- Suggest study breaks, self-care activities, and time management tips\n")
                base.append("- Share motivational quotes and positive affirmations\n")
                base.append("- Gently encourage seeking professional help if they express serious distress\n")
                base.append("- Be warm, caring, and never dismissive\n")
                base.append("Use a gentle, supportive tone with emojis where appropriate. You are NOT a therapist — you are a supportive friend.")
            }
        }

        val note = selectedNoteContext
        if (note != null) {
            base.append("\n\nThe student has selected the following study note as active context:\n")
            base.append("Note Title: ${note.title}\n")
            base.append("Course/Subject: ${note.courseName}\n")
            base.append("Content of Note:\n\"\"\"\n${note.content}\n\"\"\"\n")
            base.append("Please focus your answers around this note when relevant, or answer general academic questions if asked.")
        } else if (fileBase64 != null && fileName != null) {
            base.append("\n\nThe student has attached a file: $fileName (${fileMime ?: "unknown"}).\n")
            base.append("Since you can see the file content via inlineData in the conversation, please use it as the primary context for your response.\n")
            if (chatMode == ChatMode.Quiz) {
                base.append("Generate 5-10 quiz questions based on the attached file content.\n")
            } else if (chatMode == ChatMode.ExplainLecture) {
                base.append("Thoroughly explain and summarize the content of the attached file.\n")
            }
        }

        return base.toString()
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }
}
