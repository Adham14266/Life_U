package com.example.data.api

import com.example.config.BackendConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// Auth DTOs
@JsonClass(generateAdapter = true)
data class RegisterDto(
    @param:Json(name = "username") val username: String,
    @param:Json(name = "email") val email: String,
    @param:Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class LoginDto(
    @param:Json(name = "usernameOrEmail") val usernameOrEmail: String,
    @param:Json(name = "password") val password: String,
    @param:Json(name = "email") val email: String? = null,
    @param:Json(name = "identifier") val identifier: String? = null
)

@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @param:Json(name = "token") val token: String,
    @param:Json(name = "username") val username: String,
    @param:Json(name = "email") val email: String,
    @param:Json(name = "role") val role: String,
    @param:Json(name = "expiresAt") val expiresAt: String
)

@JsonClass(generateAdapter = true)
data class GoogleLoginDto(
    @param:Json(name = "idToken") val idToken: String,
    @param:Json(name = "email") val email: String? = null,
    @param:Json(name = "name") val name: String? = null
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "username") val username: String,
    @param:Json(name = "email") val email: String,
    @param:Json(name = "role") val role: String,
    @param:Json(name = "university") val university: String = "",
    @param:Json(name = "faculty") val faculty: String = ""
)

// Tasks DTOs
@JsonClass(generateAdapter = true)
data class TaskDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "priority") val priority: String,
    @param:Json(name = "isCompleted") val isCompleted: Boolean,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "dueDate") val dueDate: String
)

@JsonClass(generateAdapter = true)
data class CreateTaskDto(
    @param:Json(name = "title") val title: String,
    @param:Json(name = "priority") val priority: String,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "dueDate") val dueDate: String
)

// Classes DTOs
@JsonClass(generateAdapter = true)
data class ClassDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "name") val name: String,
    @param:Json(name = "timeRange") val timeRange: String,
    @param:Json(name = "dayOfWeek") val dayOfWeek: String,
    @param:Json(name = "type") val type: String
)

@JsonClass(generateAdapter = true)
data class CreateClassDto(
    @param:Json(name = "name") val name: String,
    @param:Json(name = "timeRange") val timeRange: String,
    @param:Json(name = "dayOfWeek") val dayOfWeek: String,
    @Json(name = "type") val type: String
)

// Grades DTOs
@JsonClass(generateAdapter = true)
data class GradeDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "courseName") val courseName: String,
    @param:Json(name = "gradeLetter") val gradeLetter: String,
    @param:Json(name = "creditHours") val creditHours: Int,
    @param:Json(name = "term") val term: String
)

@JsonClass(generateAdapter = true)
data class CreateGradeDto(
    @param:Json(name = "courseName") val courseName: String,
    @param:Json(name = "gradeLetter") val gradeLetter: String,
    @param:Json(name = "creditHours") val creditHours: Int,
    @param:Json(name = "term") val term: String
)

// Notes DTOs
@JsonClass(generateAdapter = true)
data class NoteDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "content") val content: String,
    @param:Json(name = "courseName") val courseName: String,
    @param:Json(name = "dateCreated") val dateCreated: String
)

@JsonClass(generateAdapter = true)
data class CreateNoteDto(
    @param:Json(name = "title") val title: String,
    @param:Json(name = "content") val content: String,
    @param:Json(name = "courseName") val courseName: String
)

// Resources DTOs
@JsonClass(generateAdapter = true)
data class ResourceDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "url") val url: String,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "notes") val notes: String,
    @param:Json(name = "courseName") val courseName: String,
    @param:Json(name = "dateAdded") val dateAdded: String
)

@JsonClass(generateAdapter = true)
data class CreateResourceDto(
    @param:Json(name = "title") val title: String,
    @param:Json(name = "url") val url: String,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "notes") val notes: String,
    @param:Json(name = "courseName") val courseName: String
)

// Chat DTOs
@JsonClass(generateAdapter = true)
data class ChatMessageDto(
    @param:Json(name = "message") val message: String
)

@JsonClass(generateAdapter = true)
data class ChatResponseDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "userMessage") val userMessage: String,
    @param:Json(name = "botResponse") val botResponse: String,
    @param:Json(name = "timestamp") val timestamp: String
)

@JsonClass(generateAdapter = true)
data class ChatHistoryDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "userMessage") val userMessage: String,
    @param:Json(name = "botResponse") val botResponse: String,
    @param:Json(name = "timestamp") val timestamp: String
)

@JsonClass(generateAdapter = true)
data class SystemPromptDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "name") val name: String,
    @param:Json(name = "prompt") val prompt: String
)

// Focus DTOs
@JsonClass(generateAdapter = true)
data class FocusSessionDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "duration") val duration: Int,
    @param:Json(name = "type") val type: String,
    @param:Json(name = "completedAt") val completedAt: String
)

@JsonClass(generateAdapter = true)
data class CreateFocusSessionDto(
    @param:Json(name = "duration") val duration: Int,
    @param:Json(name = "type") val type: String
)

@JsonClass(generateAdapter = true)
data class FocusStatsDto(
    @param:Json(name = "totalSessions") val totalSessions: Int,
    @param:Json(name = "totalMinutes") val totalMinutes: Int,
    @param:Json(name = "averageSessionLength") val averageSessionLength: Double,
    @param:Json(name = "currentStreak") val currentStreak: Int
)

// Auth Settings DTOs
@JsonClass(generateAdapter = true)
data class UserSettingsDto(
    @param:Json(name = "theme") val theme: String,
    @param:Json(name = "language") val language: String,
    @param:Json(name = "notificationsEnabled") val notificationsEnabled: Boolean
)

@JsonClass(generateAdapter = true)
data class AvatarUrlDto(
    @param:Json(name = "url") val url: String
)

// Notifications DTOs
@JsonClass(generateAdapter = true)
data class DeviceTokenDto(
    @param:Json(name = "token") val token: String,
    @param:Json(name = "platform") val platform: String
)

@JsonClass(generateAdapter = true)
data class NotificationSettingsDto(
    @param:Json(name = "taskReminders") val taskReminders: Boolean,
    @param:Json(name = "classReminders") val classReminders: Boolean,
    @param:Json(name = "gradeUpdates") val gradeUpdates: Boolean,
    @param:Json(name = "studyReminders") val studyReminders: Boolean
)

// Finance Summary DTO
@JsonClass(generateAdapter = true)
data class FinanceSummaryDto(
    @param:Json(name = "totalIncome") val totalIncome: Double,
    @param:Json(name = "totalExpenses") val totalExpenses: Double,
    @param:Json(name = "balance") val balance: Double,
    @param:Json(name = "budgetStatus") val budgetStatus: String
)

// Sync DTOs
@JsonClass(generateAdapter = true)
data class SyncDeltaDto(
    @param:Json(name = "tasks") val tasks: List<TaskDto>,
    @param:Json(name = "classes") val classes: List<ClassDto>,
    @param:Json(name = "notes") val notes: List<NoteDto>,
    @param:Json(name = "grades") val grades: List<GradeDto>,
    @param:Json(name = "resources") val resources: List<ResourceDto>,
    @param:Json(name = "transactions") val transactions: List<TransactionDto>,
    @param:Json(name = "serverTimestamp") val serverTimestamp: String
)

@JsonClass(generateAdapter = true)
data class SyncBatchDto(
    @param:Json(name = "tasks") val tasks: List<TaskDto>? = null,
    @param:Json(name = "classes") val classes: List<ClassDto>? = null,
    @param:Json(name = "notes") val notes: List<NoteDto>? = null,
    @param:Json(name = "grades") val grades: List<GradeDto>? = null,
    @param:Json(name = "resources") val resources: List<ResourceDto>? = null,
    @param:Json(name = "transactions") val transactions: List<TransactionDto>? = null
)

@JsonClass(generateAdapter = true)
data class SyncBatchResponseDto(
    @param:Json(name = "syncedCount") val syncedCount: Int,
    @param:Json(name = "serverTimestamp") val serverTimestamp: String
)

// Profile Update DTOs
@JsonClass(generateAdapter = true)
data class UpdateProfileDto(
    @param:Json(name = "username") val username: String,
    @param:Json(name = "university") val university: String? = null,
    @param:Json(name = "faculty") val faculty: String? = null,
    @param:Json(name = "studyHours") val studyHours: Int = 0,
    @param:Json(name = "deansListProgress") val deansListProgress: Float = 0f,
    @param:Json(name = "monthlyIncome") val monthlyIncome: Double = 0.0,
    @param:Json(name = "monthlyBudgetLimit") val monthlyBudgetLimit: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class ChangePasswordDto(
    @param:Json(name = "currentPassword") val currentPassword: String,
    @param:Json(name = "newPassword") val newPassword: String
)

@JsonClass(generateAdapter = true)
data class ResetPasswordDto(
    @param:Json(name = "email") val email: String,
    @param:Json(name = "newPassword") val newPassword: String
)

// Dashboard Stats DTO
@JsonClass(generateAdapter = true)
data class DashboardStatsDto(
    @param:Json(name = "totalTasks") val totalTasks: Int,
    @param:Json(name = "completedTasks") val completedTasks: Int,
    @param:Json(name = "totalClasses") val totalClasses: Int,
    @param:Json(name = "currentGpa") val currentGpa: Double,
    @param:Json(name = "totalStudyHours") val totalStudyHours: Double,
    @param:Json(name = "upcomingDeadlines") val upcomingDeadlines: Int
)

// Transactions DTOs
@JsonClass(generateAdapter = true)
data class TransactionDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "amount") val amount: Double,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "dateText") val dateText: String,
    @param:Json(name = "timestamp") val timestamp: Long
)

@JsonClass(generateAdapter = true)
data class CreateTransactionDto(
    @param:Json(name = "title") val title: String,
    @param:Json(name = "amount") val amount: Double,
    @param:Json(name = "category") val category: String,
    @param:Json(name = "dateText") val dateText: String
)

// Subject DTOs
@JsonClass(generateAdapter = true)
data class SubjectDto(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "name") val name: String,
    @param:Json(name = "color") val color: String,
    @param:Json(name = "icon") val icon: String? = null
)

@JsonClass(generateAdapter = true)
data class CreateSubjectDto(
    @param:Json(name = "name") val name: String,
    @param:Json(name = "color") val color: String,
    @param:Json(name = "icon") val icon: String? = null
)

interface StudyAppApiService {
    // Auth endpoints
    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterDto): AuthResponseDto

    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginDto): AuthResponseDto

    @GET("api/Auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): UserDto

    @POST("api/Auth/google-login")
    suspend fun googleLogin(@Body request: GoogleLoginDto): AuthResponseDto

    @PUT("api/Auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body profile: UpdateProfileDto
    ): UserDto

    @PUT("api/Auth/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordDto
    ): Unit

    // Dashboard endpoints
    @GET("api/Dashboard/stats")
    suspend fun getDashboardStats(
        @Header("Authorization") token: String
    ): DashboardStatsDto

    // Tasks endpoints
    @GET("api/Tasks")
    suspend fun getTasks(@Header("Authorization") token: String): List<TaskDto>

    @GET("api/Tasks/{id}")
    suspend fun getTask(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): TaskDto

    @POST("api/Tasks")
    suspend fun createTask(
        @Header("Authorization") token: String,
        @Body task: CreateTaskDto
    ): TaskDto

    @PUT("api/Tasks/{id}")
    suspend fun updateTask(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body task: TaskDto
    ): Unit

    @DELETE("api/Tasks/{id}")
    suspend fun deleteTask(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Unit

    // Classes endpoints
    @GET("api/Classes")
    suspend fun getClasses(@Header("Authorization") token: String): List<ClassDto>

    @GET("api/Classes/{id}")
    suspend fun getClass(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): ClassDto

    @POST("api/Classes")
    suspend fun createClass(
        @Header("Authorization") token: String,
        @Body classData: CreateClassDto
    ): ClassDto

    @PUT("api/Classes/{id}")
    suspend fun updateClass(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body classData: ClassDto
    ): Unit

    @DELETE("api/Classes/{id}")
    suspend fun deleteClass(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Unit

    // Grades endpoints
    @GET("api/Grades")
    suspend fun getGrades(@Header("Authorization") token: String): List<GradeDto>

    @GET("api/Grades/{id}")
    suspend fun getGrade(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): GradeDto

    @POST("api/Grades")
    suspend fun createGrade(
        @Header("Authorization") token: String,
        @Body grade: CreateGradeDto
    ): GradeDto

    @PUT("api/Grades/{id}")
    suspend fun updateGrade(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body grade: GradeDto
    ): Unit

    @DELETE("api/Grades/{id}")
    suspend fun deleteGrade(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Unit

    // Notes endpoints
    @GET("api/Notes")
    suspend fun getNotes(@Header("Authorization") token: String): List<NoteDto>

    @GET("api/Notes/{id}")
    suspend fun getNote(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): NoteDto

    @POST("api/Notes")
    suspend fun createNote(
        @Header("Authorization") token: String,
        @Body note: CreateNoteDto
    ): NoteDto

    @PUT("api/Notes/{id}")
    suspend fun updateNote(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body note: NoteDto
    ): Unit

    @DELETE("api/Notes/{id}")
    suspend fun deleteNote(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Unit

    // Resources endpoints
    @GET("api/Resources")
    suspend fun getResources(@Header("Authorization") token: String): List<ResourceDto>

    @GET("api/Resources/{id}")
    suspend fun getResource(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): ResourceDto

    @POST("api/Resources")
    suspend fun createResource(
        @Header("Authorization") token: String,
        @Body resource: CreateResourceDto
    ): ResourceDto

    @PUT("api/Resources/{id}")
    suspend fun updateResource(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body resource: ResourceDto
    ): Unit

    @DELETE("api/Resources/{id}")
    suspend fun deleteResource(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Unit

    // Transactions endpoints
    @GET("api/Transactions")
    suspend fun getTransactions(@Header("Authorization") token: String): List<TransactionDto>

    @GET("api/Transactions/{id}")
    suspend fun getTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): TransactionDto

    @POST("api/Transactions")
    suspend fun createTransaction(
        @Header("Authorization") token: String,
        @Body transaction: CreateTransactionDto
    ): TransactionDto

    @PUT("api/Transactions/{id}")
    suspend fun updateTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body transaction: TransactionDto
    ): Unit

    @DELETE("api/Transactions/{id}")
    suspend fun deleteTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Unit

    @GET("api/Transactions/category/{category}")
    suspend fun getTransactionsByCategory(
        @Header("Authorization") token: String,
        @Path("category") category: String
    ): List<TransactionDto>

    // Filter endpoints
    @GET("api/Tasks/category/{category}")
    suspend fun getTasksByCategory(
        @Header("Authorization") token: String,
        @Path("category") category: String
    ): List<TaskDto>

    @GET("api/Classes/day/{dayOfWeek}")
    suspend fun getClassesByDay(
        @Header("Authorization") token: String,
        @Path("dayOfWeek") dayOfWeek: String
    ): List<ClassDto>

    @GET("api/Notes/course/{courseName}")
    suspend fun getNotesByCourse(
        @Header("Authorization") token: String,
        @Path("courseName") courseName: String
    ): List<NoteDto>

    @GET("api/Resources/category/{category}")
    suspend fun getResourcesByCategory(
        @Header("Authorization") token: String,
        @Path("category") category: String
    ): List<ResourceDto>

    @GET("api/Grades/term/{term}")
    suspend fun getGradesByTerm(
        @Header("Authorization") token: String,
        @Path("term") term: String
    ): List<GradeDto>

    // Auth extended endpoints
    @GET("api/Auth/avatar")
    suspend fun getAvatar(
        @Header("Authorization") token: String
    ): AvatarUrlDto

    @POST("api/Auth/avatar")
    suspend fun uploadAvatar(
        @Header("Authorization") token: String,
        @Body avatar: AvatarUrlDto
    ): AvatarUrlDto

    @POST("api/Auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Unit

    @POST("api/Auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordDto)

    @POST("api/Auth/change-password")
    suspend fun changePasswordPost(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordDto
    ): Unit

    @DELETE("api/Auth/account")
    suspend fun deleteAccount(
        @Header("Authorization") token: String
    ): Unit

    @GET("api/Auth/settings")
    suspend fun getSettings(
        @Header("Authorization") token: String
    ): UserSettingsDto

    @POST("api/Auth/settings")
    suspend fun updateSettings(
        @Header("Authorization") token: String,
        @Body settings: UserSettingsDto
    ): UserSettingsDto

    // Chat endpoints
    @POST("api/Chat/message")
    suspend fun sendChatMessage(
        @Header("Authorization") token: String,
        @Body message: ChatMessageDto
    ): ChatResponseDto

    @GET("api/Chat/history")
    suspend fun getChatHistory(
        @Header("Authorization") token: String
    ): List<ChatHistoryDto>

    @DELETE("api/Chat/history")
    suspend fun clearChatHistory(
        @Header("Authorization") token: String
    ): Unit

    @GET("api/Chat/prompts")
    suspend fun getSystemPrompts(
        @Header("Authorization") token: String
    ): List<SystemPromptDto>

    // Focus endpoints
    @GET("api/Focus/history")
    suspend fun getFocusHistory(
        @Header("Authorization") token: String
    ): List<FocusSessionDto>

    @POST("api/Focus/sessions")
    suspend fun createFocusSession(
        @Header("Authorization") token: String,
        @Body session: CreateFocusSessionDto
    ): FocusSessionDto

    @GET("api/Focus/stats")
    suspend fun getFocusStats(
        @Header("Authorization") token: String
    ): FocusStatsDto

    // Resources file management endpoints
    @Multipart
    @POST("api/Resources/{resourceId}/upload")
    suspend fun uploadResourceFile(
        @Header("Authorization") token: String,
        @Path("resourceId") resourceId: Int,
        @Part file: MultipartBody.Part
    ): ResourceDto

    @GET("api/Resources/{resourceId}/download")
    suspend fun downloadResourceFile(
        @Header("Authorization") token: String,
        @Path("resourceId") resourceId: Int
    ): ResponseBody

    @DELETE("api/Resources/{resourceId}/file")
    suspend fun deleteResourceFile(
        @Header("Authorization") token: String,
        @Path("resourceId") resourceId: Int
    ): Unit

    // Notifications endpoints
    @POST("api/Notifications/register")
    suspend fun registerDeviceToken(
        @Header("Authorization") token: String,
        @Body deviceToken: DeviceTokenDto
    ): Unit

    @POST("api/Notifications/settings")
    suspend fun updateNotificationSettings(
        @Header("Authorization") token: String,
        @Body settings: NotificationSettingsDto
    ): NotificationSettingsDto

    @GET("api/Notifications/settings")
    suspend fun getNotificationSettings(
        @Header("Authorization") token: String
    ): NotificationSettingsDto

    // Finance endpoint
    @GET("api/Finance/summary")
    suspend fun getFinanceSummary(
        @Header("Authorization") token: String
    ): FinanceSummaryDto

    // Sync endpoints
    @GET("api/Sync/delta")
    suspend fun getSyncDelta(
        @Header("Authorization") token: String,
        @Query("since") since: String
    ): SyncDeltaDto

    @POST("api/Sync/batch")
    suspend fun syncBatch(
        @Header("Authorization") token: String,
        @Body batch: SyncBatchDto
    ): SyncBatchResponseDto

    // Subjects endpoints
    @GET("api/Subjects")
    suspend fun getSubjects(@Header("Authorization") token: String): List<SubjectDto>

    @GET("api/Subjects/{id}")
    suspend fun getSubject(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): SubjectDto

    @POST("api/Subjects")
    suspend fun createSubject(
        @Header("Authorization") token: String,
        @Body subject: CreateSubjectDto
    ): SubjectDto

    @PUT("api/Subjects/{id}")
    suspend fun updateSubject(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body subject: SubjectDto
    ): Unit

    @DELETE("api/Subjects/{id}")
    suspend fun deleteSubject(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Unit
}

object StudyAppRetrofitClient {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(okhttp3.logging.HttpLoggingInterceptor().apply {
            level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val service: StudyAppApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BackendConfig.currentBackendUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(StudyAppApiService::class.java)
    }
}
