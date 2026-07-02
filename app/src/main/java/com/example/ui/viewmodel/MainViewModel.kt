package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.*
import com.example.data.local.*
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppScreen {
    Splash,
    OnboardingStage1,
    OnboardingStage2,
    OnboardingStage3,
    Login,
    SignUp,
    Main
}

enum class MainTab {
    Dashboard,
    Tutor,
    Schedule,
    Finances,
    Profile
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val time: String,
    val attachedFile: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = StudyRepository(
        database.taskDao(),
        database.classDao(),
        database.transactionDao(),
        database.studyNoteDao(),
        database.courseGradeDao(),
        database.studyResourceDao()
    )

    // Screen State Navigation
    var currentScreen by mutableStateOf(AppScreen.Splash)
        private set

    var currentTab by mutableStateOf(MainTab.Dashboard)
        private set

    // User Profile Information
    var userName by mutableStateOf("Alex Johnson")
    var userUniversity by mutableStateOf("Stanford University")
    
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
            if (gradeList.isEmpty()) return "3.80" // Realistic initial default if empty
            var totalPoints = 0.0
            var totalCredits = 0
            for (g in gradeList) {
                totalPoints += getGradePoints(g.gradeLetter) * g.creditHours
                totalCredits += g.creditHours
            }
            return if (totalCredits > 0) {
                String.format(Locale.US, "%.2f", totalPoints / totalCredits)
            } else {
                "3.80"
            }
        }

    var userStudyHours by mutableStateOf("320")
    var deansListProgress by mutableStateOf(0.85f)
    var isDarkTheme by mutableStateOf(false)
    
    // Budget & Finances State
    var monthlyIncome by mutableStateOf(2500.00)
    var monthlyBudgetLimit by mutableStateOf(1500.00)
    var showEditBudgetDialog by mutableStateOf(false)

    // Enhanced Pomodoro Study Timer State
    enum class PomodoroMode {
        WORK, SHORT_BREAK, LONG_BREAK
    }

    data class FocusSession(
        val id: String = java.util.UUID.randomUUID().toString(),
        val subject: String,
        val durationMinutes: Int,
        val mode: PomodoroMode,
        val timestamp: String
    )

    var selectedStudySubject by mutableStateOf("Advanced Biology")
    var pomodoroMode by mutableStateOf(PomodoroMode.WORK)
    
    var workDurationMinutes by mutableStateOf(25)
    var shortBreakMinutes by mutableStateOf(5)
    var longBreakMinutes by mutableStateOf(15)

    var showSessionCompletedDialog by mutableStateOf(false)
    var lastCompletedSessionType by mutableStateOf("")

    var focusHistory by mutableStateOf<List<FocusSession>>(
        listOf(
            FocusSession(
                subject = "Advanced Biology",
                durationMinutes = 25,
                mode = PomodoroMode.WORK,
                timestamp = "Today • 10:45 AM"
            ),
            FocusSession(
                subject = "Intro to Economics",
                durationMinutes = 25,
                mode = PomodoroMode.WORK,
                timestamp = "Yesterday • 03:15 PM"
            )
        )
    )

    var focusTimerLeftSeconds by mutableStateOf(25 * 60)
    var isTimerRunning by mutableStateOf(false)
    private var timerJob: Job? = null

    // Room Database State Flows
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

    val urgentTasks: StateFlow<List<Task>> = repository.allTasks
        .map { list -> list.filter { !it.isCompleted && isDueWithin24Hours(it.dueDate) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val classes: StateFlow<List<ClassEvent>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<FinanceTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<StudyNote>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val grades: StateFlow<List<CourseGrade>> = repository.allGrades
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val resources: StateFlow<List<StudyResource>> = repository.allResources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Feedback & Dialog states
    var showAddTaskDialog by mutableStateOf(false)
    var showAddClassDialog by mutableStateOf(false)
    var showAddTransactionDialog by mutableStateOf(false)
    var showAddNoteDialog by mutableStateOf(false)
    var showAddGradeDialog by mutableStateOf(false)
    var showAddResourceDialog by mutableStateOf(false)

    var selectedNoteContext by mutableStateOf<StudyNote?>(null)

    fun addNote(title: String, content: String, courseName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateStr = SimpleDateFormat("MMM d", Locale.US).format(Date())
            repository.insertNote(StudyNote(title = title, content = content, courseName = courseName, dateCreated = dateStr))
        }
    }

    fun addResource(title: String, url: String, category: String, notes: String = "", courseName: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date())
            repository.insertResource(
                StudyResource(
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
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertGrade(CourseGrade(courseName = courseName, gradeLetter = gradeLetter, creditHours = creditHours, term = term))
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

    // Stitch AI Chat States
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    var isStitchThinking by mutableStateOf(false)
        private set

    init {
        // Pre-populate data if empty
        viewModelScope.launch(Dispatchers.IO) {
            repository.allTasks.first().let { currentTasks ->
                if (currentTasks.isEmpty()) {
                    populateInitialData()
                }
            }
        }
        setupInitialChat()
    }

    private suspend fun populateInitialData() {
        // Tasks
        repository.insertTask(Task(title = "Review Calculus Lecture 4", priority = "High", isCompleted = false, category = "Calculus", dueDate = "Oct 12"))
        repository.insertTask(Task(title = "Submit History Essay Draft", priority = "Done", isCompleted = true, category = "History", dueDate = "Oct 15"))
        repository.insertTask(Task(title = "Pre-lab Quiz Biology", priority = "Medium", isCompleted = false, category = "Biology", dueDate = "Oct 20"))

        // Timetable Classes
        repository.insertClass(ClassEvent(name = "Calc 101", timeRange = "09:00 AM - 10:30 AM", dayOfWeek = "Mon", type = "Lecture"))
        repository.insertClass(ClassEvent(name = "Bio Lab", timeRange = "09:00 AM - 11:00 AM", dayOfWeek = "Wed", type = "Lab"))
        repository.insertClass(ClassEvent(name = "History", timeRange = "11:00 AM - 12:30 PM", dayOfWeek = "Tue", type = "Lecture"))
        repository.insertClass(ClassEvent(name = "History", timeRange = "11:00 AM - 12:30 PM", dayOfWeek = "Thu", type = "Lecture"))

        // Financial Transactions
        repository.insertTransaction(FinanceTransaction(title = "Campus Bookstore", amount = -45.00, category = "Books", dateText = "Today • 12:45 PM"))
        repository.insertTransaction(FinanceTransaction(title = "Monthly Stipend", amount = 1200.00, category = "Income", dateText = "Yesterday • 09:00 AM"))
        repository.insertTransaction(FinanceTransaction(title = "University Cafeteria", amount = -12.50, category = "Food", dateText = "Oct 24 • 01:15 PM"))

        // Study Notes
        repository.insertNote(StudyNote(title = "Photosynthesis Key Concepts", content = "Photosynthesis takes place in chloroplasts of plant cells. It consists of two stages: 1) Light-dependent reactions: absorb light energy in thylakoid membranes to generate ATP and NADPH, releasing oxygen as a byproduct. 2) Calvin Cycle (Light-independent): occurs in the stroma, uses ATP and NADPH to fix CO2 into G3P (sugars). Key pigment is Chlorophyll a.", courseName = "Biology", dateCreated = "Oct 10"))
        repository.insertNote(StudyNote(title = "Calculus Derivative Rules", content = "Essential differentiation formulas: Power Rule: d/dx(x^n) = n*x^(n-1). Product Rule: d/dx(f*g) = f'g + fg'. Quotient Rule: d/dx(f/g) = (f'g - fg')/g^2. Chain Rule: d/dx(f(g(x))) = f'(g(x)) * g'(x). Derivatives of trigonometric functions: d/dx(sin x) = cos x, d/dx(cos x) = -sin x, d/dx(tan x) = sec^2 x.", courseName = "Calculus", dateCreated = "Oct 12"))

        // Course Grades for GPA Calculation
        repository.insertGrade(CourseGrade(courseName = "Calculus I", gradeLetter = "A", creditHours = 4, term = "Fall 2025"))
        repository.insertGrade(CourseGrade(courseName = "Introduction to Biology", gradeLetter = "A-", creditHours = 4, term = "Fall 2025"))
        repository.insertGrade(CourseGrade(courseName = "US History to 1865", gradeLetter = "B+", creditHours = 3, term = "Fall 2025"))
        repository.insertGrade(CourseGrade(courseName = "General Chemistry", gradeLetter = "A", creditHours = 4, term = "Winter 2026"))
        repository.insertGrade(CourseGrade(courseName = "English Literature", gradeLetter = "A+", creditHours = 3, term = "Winter 2026"))

        // Study Resources (Resource Vault)
        repository.insertResource(StudyResource(title = "OpenStax Calculus Volume 1 Textbook", url = "https://openstax.org/details/books/calculus-volume-1", category = "Textbooks", notes = "Free, peer-reviewed textbook covering single-variable calculus functions, limits, derivatives, and integration.", courseName = "Calculus", dateAdded = "Oct 12, 2025"))
        repository.insertResource(StudyResource(title = "MIT OpenCourseWare Single Variable Calculus", url = "https://ocw.mit.edu/courses/18-01sc-single-variable-calculus-fall-2010/", category = "Study Materials", notes = "Excellent video lectures, recitation guides, and problem sets with solutions. Highly recommended for exam prep.", courseName = "Calculus", dateAdded = "Oct 14, 2025"))
        repository.insertResource(StudyResource(title = "The Calvin Cycle: Light-Independent Reactions Review", url = "https://www.ncbi.nlm.nih.gov/books/NBK21162/", category = "Academic Articles", notes = "Detailed biochemistry explanation of carbon fixation, reduction, and regeneration phases.", courseName = "Biology", dateAdded = "Oct 15, 2025"))
    }

    private fun setupInitialChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                text = "Hey Stitch! Can you explain the difference between Mitosis and Meiosis based on this chapter?",
                isUser = true,
                time = "09:41 AM",
                attachedFile = "Biology_Ch4.pdf"
            ),
            ChatMessage(
                text = "Great question! Based on Chapter 4 of your Biology notes, here is a simple breakdown:\n\n• **Mitosis:** Produces two identical daughter cells for growth and tissue repair.\n• **Meiosis:** A specialized division that results in four unique daughter cells (gametes) with half the chromosome count.\n\nWould you like me to create a comparison table for your exam review?",
                isUser = false,
                time = "09:42 AM"
            )
        )
    }

    // Navigation Methods
    fun navigateTo(screen: AppScreen) {
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
                    try {
                        val currentHours = userStudyHours.toInt()
                        userStudyHours = (currentHours + 1).toString()
                    } catch (e: Exception) {
                        userStudyHours = "321"
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
                    
                    // Auto-advance to short break
                    pomodoroMode = PomodoroMode.SHORT_BREAK
                    focusTimerLeftSeconds = shortBreakMinutes * 60
                } else {
                    lastCompletedSessionType = if (pomodoroMode == PomodoroMode.SHORT_BREAK) "Short Break" else "Long Break"
                    showSessionCompletedDialog = true
                    
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
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(Task(title = title, priority = priority, isCompleted = false, category = category, dueDate = dueDate))
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
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertClass(ClassEvent(name = name, timeRange = timeRange, dayOfWeek = dayOfWeek, type = type))
        }
    }

    fun addTransaction(title: String, amount: Double, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isIncome = category == "Income"
            val signedAmount = if (isIncome) Math.abs(amount) else -Math.abs(amount)
            val sdf = SimpleDateFormat("MMM dd • hh:mm a", Locale.getDefault())
            val dateText = sdf.format(Date())
            repository.insertTransaction(FinanceTransaction(title = title, amount = signedAmount, category = category, dateText = dateText))
        }
    }

    fun deleteTransaction(transaction: FinanceTransaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(transaction)
        }
    }

    // Chat Actions
    fun sendMessageToStitch(text: String, attachedFile: String? = null) {
        if (text.trim().isEmpty() && attachedFile == null) return

        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeNow = sdf.format(Date())

        val userMsg = ChatMessage(text = text, isUser = true, time = timeNow, attachedFile = attachedFile)
        _chatMessages.value = _chatMessages.value + userMsg

        isStitchThinking = true

        viewModelScope.launch {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                delay(1500)
                val responseText = "Hi! I would love to answer that, but my Gemini AI API Key is not configured yet. Please use the **Secrets panel** in the AI Studio UI to set your `GEMINI_API_KEY` securely. \n\n" +
                        "Once configured, I can explain topics from your PDFs, create summaries, build study tables, and act as your custom intelligent study companion!"
                val stitchMsg = ChatMessage(text = responseText, isUser = false, time = sdf.format(Date()))
                _chatMessages.value = _chatMessages.value + stitchMsg
                isStitchThinking = false
                return@launch
            }

            // Build request contents for conversation history
            val moshiContents = _chatMessages.value.takeLast(10).map { msg ->
                MoshiContent(parts = listOf(MoshiPart(text = msg.text)))
            }

            val systemText = StringBuilder("You are Stitch, a friendly, supportive, and intelligent university study companion for Life U. Help the student with their courses, summarize lessons, generate flashcards/quizzes, and explain concepts simply. Always sound approachable, clear, and encouraging. Use elegant Markdown bullet points or clear tables to make learning visual and easy.")
            
            selectedNoteContext?.let { note ->
                systemText.append("\n\nThe student has selected the following study note as active context:\n")
                systemText.append("Note Title: ${note.title}\n")
                systemText.append("Course/Subject: ${note.courseName}\n")
                systemText.append("Content of Note:\n\"\"\"\n${note.content}\n\"\"\"\n")
                systemText.append("Please focus your answers around this note when relevant, or answer general academic questions if asked.")
            }

            val request = MoshiGenerateContentRequest(
                contents = moshiContents,
                systemInstruction = MoshiContent(
                    parts = listOf(MoshiPart(text = systemText.toString()))
                ),
                generationConfig = MoshiGenerationConfig(temperature = 0.7f)
            )

            try {
                val response = GeminiRetrofitClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "I'm sorry, I couldn't formulate a proper response. Please try asking again!"
                val stitchMsg = ChatMessage(text = responseText, isUser = false, time = sdf.format(Date()))
                _chatMessages.value = _chatMessages.value + stitchMsg
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    text = "Oh no! I encountered an error while trying to connect to my brain. Details: ${e.localizedMessage ?: "Unknown network issue"}. Please check your internet connection or your API key settings.",
                    isUser = false,
                    time = sdf.format(Date())
                )
                _chatMessages.value = _chatMessages.value + errorMsg
            } finally {
                isStitchThinking = false
            }
        }
    }
}
