# Backend Integration Guide

This guide explains how to use the backend API integration in the Life-U Android app.

## Overview

The app now includes a complete backend integration with the following components:

- **StudyAppApiService**: Retrofit interface for all backend API calls
- **SyncedStudyRepository**: Repository that handles both local and backend data synchronization
- **BackendConfig**: Configuration management for backend URLs

## Backend API Endpoints

The backend supports the following REST API endpoints:

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user

### Tasks
- `GET /api/tasks` - Get all tasks
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update a task
- `DELETE /api/tasks/{id}` - Delete a task

### Classes
- `GET /api/classes` - Get all classes
- `POST /api/classes` - Create a new class
- `DELETE /api/classes/{id}` - Delete a class

### Grades
- `GET /api/grades` - Get all grades
- `POST /api/grades` - Create a new grade
- `DELETE /api/grades/{id}` - Delete a grade

### Notes
- `GET /api/notes` - Get all notes
- `POST /api/notes` - Create a new note
- `DELETE /api/notes/{id}` - Delete a note

### Resources
- `GET /api/resources` - Get all resources
- `POST /api/resources` - Create a new resource
- `DELETE /api/resources/{id}` - Delete a resource

### Transactions
- `GET /api/transactions` - Get all transactions
- `POST /api/transactions` - Create a new transaction
- `DELETE /api/transactions/{id}` - Delete a transaction

## Setup Instructions

### 1. Configure Backend URL

The app uses `http://10.0.2.2:5000/` by default for Android emulator development.

To change the backend URL:

```kotlin
import com.example.config.BackendConfig

// Development URL
BackendConfig.setBackendUrl("http://10.0.2.2:5000/")

// Or production URL
BackendConfig.setBackendUrl("https://your-backend-domain.com/")
```

### 2. Inject the Synced Repository

Update your dependency injection to use `SyncedStudyRepository`:

```kotlin
// Before (local only)
val repository = StudyRepository(taskDao, classDao, transactionDao, studyNoteDao, courseGradeDao, studyResourceDao)

// After (local + backend sync)
val repository = SyncedStudyRepository(
    taskDao, classDao, transactionDao, studyNoteDao, courseGradeDao, studyResourceDao,
    StudyAppRetrofitClient.service
)
```

### 3. Handle Authentication

After login, set the auth token:

```kotlin
try {
    val authResponse = repository.login("user@example.com", "password")
    repository.setAuthToken(authResponse.token)
    // Navigate to main screen
} catch (e: Exception) {
    // Handle login error
}
```

### 4. Sync Data from Backend

After authentication, sync existing data:

```kotlin
viewModelScope.launch {
    repository.syncTasksFromBackend()
    repository.syncClassesFromBackend()
    repository.syncGradesFromBackend()
    // ... sync other data types
}
```

## Usage Examples

### Register and Login

```kotlin
// Register
try {
    val authResponse = repository.register(
        email = "user@example.com",
        password = "securePassword123",
        name = "John Doe"
    )
    repository.setAuthToken(authResponse.token)
} catch (e: Exception) {
    Log.e("Auth", "Registration failed", e)
}

// Login
try {
    val authResponse = repository.login(
        email = "user@example.com",
        password = "securePassword123"
    )
    repository.setAuthToken(authResponse.token)
} catch (e: Exception) {
    Log.e("Auth", "Login failed", e)
}
```

### Create a Task

```kotlin
val task = Task(
    title = "Complete assignment",
    description = "Finish the math assignment",
    dueDate = "2024-07-15",
    priority = "HIGH",
    status = "PENDING"
)

repository.insertTask(task)
// This will both save locally and sync to backend
```

### Observe Tasks Flow

```kotlin
viewModel.allTasks.collect { tasks ->
    // Update UI with tasks
}
```

### Delete a Task

```kotlin
repository.deleteTask(task)
// This will delete locally and from backend
```

## Network Error Handling

All repository methods that sync with the backend are wrapped in try-catch blocks. If the backend is unavailable:

1. Local operations complete successfully
2. Backend sync is skipped with error logged
3. Data will sync when connectivity is restored (manual sync required)

Example of manual retry:

```kotlin
val result = repository.syncTasksFromBackend()
if (result.isFailure) {
    Log.e("Sync", "Failed to sync tasks", result.exceptionOrNull())
    // Show retry UI to user
}
```

## Backend URL for Different Environments

| Environment | URL | Usage |
|---|---|---|
| Android Emulator | `http://10.0.2.2:5000/` | Local development with emulator |
| Physical Device (Local) | `http://192.168.x.x:5000/` | Local network |
| Production | `https://api.yourdomain.com/` | Deployed backend |

## Starting the Backend

The backend is a .NET 8 application located in `/Desktop/DEPI/life-u-2/backend/`

```bash
cd /Desktop/DEPI/life-u-2/backend
dotnet run --project src/StudyApp.API
```

The backend will start on `http://localhost:5000/`

## Security Notes

- Always use HTTPS in production
- Store auth tokens securely (consider using EncryptedSharedPreferences)
- Never hardcode API keys or sensitive information
- Validate all user inputs on both client and server
- Implement proper JWT token expiration and refresh logic

## Troubleshooting

### Connection Refused
- Ensure backend is running
- Check backend URL is correct
- For emulator: use `http://10.0.2.2:5000/` not `http://localhost:5000/`

### 401 Unauthorized
- Token may be expired
- Implement token refresh logic
- Check if user is properly authenticated

### Network Timeout
- Increase timeouts if backend is slow
- Check network connectivity
- Ensure backend is responsive
