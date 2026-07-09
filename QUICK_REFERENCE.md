# Quick Reference - Backend Integration

## 🎯 File Locations

### Android App (Kotlin)
```
/Users/omarnagi/Desktop/life-u/app/src/main/java/com/example/
├── data/api/StudyAppApiService.kt ................. Retrofit API Interface
├── data/repository/SyncedStudyRepository.kt ....... Local + Backend Sync
└── config/BackendConfig.kt ........................ URL Configuration
```

### Backend API (.NET C#)
```
/Users/omarnagi/Desktop/DEPI/life-u-2/backend/src/StudyApp.API/Controllers/
├── AuthController.cs ............................ Authentication
├── TasksController.cs ........................... Tasks CRUD
├── ClassesController.cs ......................... Classes CRUD
├── GradesController.cs .......................... Grades CRUD
├── NotesController.cs ........................... Notes CRUD
├── ResourcesController.cs ........................ Resources CRUD
└── TransactionsController.cs ..................... Transactions CRUD
```

## 🔌 API Endpoints (All Prefixed with `/api/`)

| Entity | Method | Endpoint |
|--------|--------|----------|
| **Auth** | POST | `/auth/register` |
| | POST | `/auth/login` |
| | GET | `/auth/profile` 🔐 |
| **Tasks** | GET | `/tasks` 🔐 |
| | GET | `/tasks/{id}` 🔐 |
| | POST | `/tasks` 🔐 |
| | PUT | `/tasks/{id}` 🔐 |
| | DELETE | `/tasks/{id}` 🔐 |
| **Classes** | GET | `/classes` 🔐 |
| | GET | `/classes/{id}` 🔐 |
| | POST | `/classes` 🔐 |
| | PUT | `/classes/{id}` 🔐 |
| | DELETE | `/classes/{id}` 🔐 |
| **Grades** | GET | `/grades` 🔐 |
| | GET | `/grades/{id}` 🔐 |
| | POST | `/grades` 🔐 |
| | PUT | `/grades/{id}` 🔐 |
| | DELETE | `/grades/{id}` 🔐 |
| **Notes** | GET | `/notes` 🔐 |
| | GET | `/notes/{id}` 🔐 |
| | POST | `/notes` 🔐 |
| | PUT | `/notes/{id}` 🔐 |
| | DELETE | `/notes/{id}` 🔐 |
| **Resources** | GET | `/resources` 🔐 |
| | GET | `/resources/{id}` 🔐 |
| | POST | `/resources` 🔐 |
| | PUT | `/resources/{id}` 🔐 |
| | DELETE | `/resources/{id}` 🔐 |
| **Transactions** | GET | `/transactions` 🔐 |
| | GET | `/transactions/{id}` 🔐 |
| | POST | `/transactions` 🔐 |
| | PUT | `/transactions/{id}` 🔐 |
| | DELETE | `/transactions/{id}` 🔐 |

🔐 = Requires Authentication (Bearer Token)

## ⚙️ Setup (3 Steps)

### Step 1: Set Backend URL
```kotlin
BackendConfig.setBackendUrl("http://10.0.2.2:5000/")  // Emulator
// OR
BackendConfig.setBackendUrl("http://192.168.1.100:5000/")  // Physical device
```

### Step 2: Initialize Repository
```kotlin
val repository = SyncedStudyRepository(
    taskDao, classDao, transactionDao, studyNoteDao, 
    courseGradeDao, studyResourceDao,
    StudyAppRetrofitClient.service
)
```

### Step 3: Login
```kotlin
val authResponse = repository.login("user@example.com", "password")
repository.setAuthToken(authResponse.token)
```

## 🔄 Common Operations

### Create
```kotlin
val task = Task(
    title = "Buy books",
    description = "For next semester",
    dueDate = "2024-07-20",
    priority = "HIGH",
    status = "PENDING"
)
repository.insertTask(task)  // Saves locally + to backend
```

### Read
```kotlin
repository.allTasks.collect { tasks ->
    // Update UI with tasks
}
```

### Update
```kotlin
val updatedTask = task.copy(status = "COMPLETED")
repository.updateTask(updatedTask)  // Updates locally + on backend
```

### Delete
```kotlin
repository.deleteTask(task)  // Deletes locally + from backend
```

### Sync from Backend
```kotlin
repository.syncTasksFromBackend()
repository.syncClassesFromBackend()
repository.syncGradesFromBackend()
repository.syncNotesFromBackend()
repository.syncResourcesFromBackend()
repository.syncTransactionsFromBackend()
```

## 🚀 Start Backend

```bash
cd /Users/omarnagi/Desktop/DEPI/life-u-2/backend
dotnet run --project src/StudyApp.API
```

Backend runs on: **http://localhost:5000/**

## 📱 Base URLs by Environment

| Environment | URL |
|---|---|
| Android Emulator | `http://10.0.2.2:5000/` |
| Physical Device (Local) | `http://192.168.x.x:5000/` |
| Production | `https://yourdomain.com/` |

## 🔒 Authentication

**Registration**
```
POST /api/auth/register
Body: { email, password, name }
Response: { token, userId, email, name }
```

**Login**
```
POST /api/auth/login
Body: { email, password }
Response: { token, userId, email, name }
```

**Using Token**
```
All requests (except register/login):
Header: Authorization: Bearer {token}
```

## 📊 Data Types

| Type | DTO | Create DTO | Fields |
|------|-----|-----------|--------|
| Task | TaskDto | CreateTaskDto | id, title, description, dueDate, priority, status |
| Class | ClassDto | CreateClassDto | id, name, instructor, schedule, location |
| Grade | GradeDto | CreateGradeDto | id, courseName, grade, weight |
| Note | NoteDto | CreateNoteDto | id, title, content, createdAt |
| Resource | ResourceDto | CreateResourceDto | id, title, url, category |
| Transaction | TransactionDto | CreateTransactionDto | id, description, amount, type, date |

## ❌ Common Errors

| Error | Cause | Solution |
|-------|-------|----------|
| Connection refused | Backend not running | `dotnet run` backend |
| 401 Unauthorized | No token | Call login first |
| 404 Not Found | Wrong endpoint | Check spelling |
| 400 Bad Request | Invalid data | Check field names |

## 📚 Documentation

- `BACKEND_INTEGRATION.md` - Full setup guide
- `API_REFERENCE.md` - Complete API docs
- `INTEGRATION_SUMMARY.md` - Overview & troubleshooting
- `FILES_CREATED.txt` - File listing

## 💡 Tips

✅ Always set backend URL before making requests  
✅ Always call login before syncing data  
✅ Use Bearer prefix for Authorization header  
✅ Check logs for error details (tag: "SyncedStudyRepository")  
✅ For emulator: use 10.0.2.2, not localhost  
✅ For physical device: use local network IP  

---

**Quick Test**: `curl http://localhost:5000/api/tasks` (after backend starts)
