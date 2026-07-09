# Backend API Reference

## API Location
**Backend Repository**: `/Users/omarnagi/Desktop/DEPI/life-u-2/backend`

### Backend Controllers Location
```
backend/
└── src/
    └── StudyApp.API/
        └── Controllers/
            ├── ApiControllerBase.cs       (Base class for all controllers)
            ├── AuthController.cs          (Authentication endpoints)
            ├── TasksController.cs         (Tasks CRUD)
            ├── ClassesController.cs       (Classes CRUD)
            ├── GradesController.cs        (Grades CRUD)
            ├── NotesController.cs         (Notes CRUD)
            ├── ResourcesController.cs     (Resources CRUD)
            └── TransactionsController.cs  (Transactions CRUD)
```

### Android API Integration
**Android App Location**: `/Users/omarnagi/Desktop/life-u`

```
app/
└── src/
    └── main/
        └── java/
            └── com/example/
                ├── data/
                │   ├── api/
                │   │   ├── StudyAppApiService.kt    (Retrofit API Interface)
                │   │   └── GeminiApiService.kt      (Gemini AI API)
                │   └── repository/
                │       ├── StudyRepository.kt        (Local only)
                │       └── SyncedStudyRepository.kt  (Local + Backend sync)
                └── config/
                    └── BackendConfig.kt              (Configuration)
```

## API Endpoints Summary

### Base URL
```
http://10.0.2.2:5000/      (Android Emulator - Development)
http://192.168.x.x:5000/   (Physical Device - Local Network)
https://yourdomain.com/    (Production)
```

### Authentication Endpoints

#### 1. Register User
```
POST /api/auth/register
Content-Type: application/json

Request Body:
{
  "email": "user@example.com",
  "password": "securePassword123",
  "name": "John Doe"
}

Response (200):
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "userId": "123",
  "email": "user@example.com",
  "name": "John Doe"
}
```

#### 2. Login User
```
POST /api/auth/login
Content-Type: application/json

Request Body:
{
  "email": "user@example.com",
  "password": "securePassword123"
}

Response (200):
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "userId": "123",
  "email": "user@example.com",
  "name": "John Doe"
}
```

#### 3. Get User Profile
```
GET /api/auth/profile
Authorization: Bearer {token}

Response (200):
{
  "id": 123,
  "email": "user@example.com",
  "name": "John Doe"
}

Response (401): Unauthorized
```

---

### Tasks Endpoints

#### 1. Get All Tasks
```
GET /api/tasks
Authorization: Bearer {token}

Response (200):
[
  {
    "id": 1,
    "title": "Complete assignment",
    "description": "Finish the math assignment",
    "dueDate": "2024-07-15",
    "priority": "HIGH",
    "status": "PENDING"
  }
]
```

#### 2. Get Task by ID
```
GET /api/tasks/{id}
Authorization: Bearer {token}

Response (200):
{
  "id": 1,
  "title": "Complete assignment",
  "description": "Finish the math assignment",
  "dueDate": "2024-07-15",
  "priority": "HIGH",
  "status": "PENDING"
}
```

#### 3. Create Task
```
POST /api/tasks
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
  "title": "Complete assignment",
  "description": "Finish the math assignment",
  "dueDate": "2024-07-15",
  "priority": "HIGH",
  "status": "PENDING"
}

Response (201):
{
  "id": 1,
  "title": "Complete assignment",
  "description": "Finish the math assignment",
  "dueDate": "2024-07-15",
  "priority": "HIGH",
  "status": "PENDING"
}
```

#### 4. Update Task
```
PUT /api/tasks/{id}
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
  "id": 1,
  "title": "Complete assignment",
  "description": "Finish the math assignment",
  "dueDate": "2024-07-15",
  "priority": "HIGH",
  "status": "COMPLETED"
}

Response (204): No Content
```

#### 5. Delete Task
```
DELETE /api/tasks/{id}
Authorization: Bearer {token}

Response (204): No Content
```

---

### Classes Endpoints

#### 1. Get All Classes
```
GET /api/classes
Authorization: Bearer {token}

Response (200):
[
  {
    "id": 1,
    "name": "Math 101",
    "instructor": "Dr. Smith",
    "schedule": "Monday, Wednesday 10:00 AM",
    "location": "Room 101"
  }
]
```

#### 2. Get Class by ID
```
GET /api/classes/{id}
Authorization: Bearer {token}
```

#### 3. Create Class
```
POST /api/classes
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
  "name": "Math 101",
  "instructor": "Dr. Smith",
  "schedule": "Monday, Wednesday 10:00 AM",
  "location": "Room 101"
}

Response (201): Returns created ClassDto
```

#### 4. Update Class
```
PUT /api/classes/{id}
Authorization: Bearer {token}
Content-Type: application/json

Request Body: ClassDto object

Response (204): No Content
```

#### 5. Delete Class
```
DELETE /api/classes/{id}
Authorization: Bearer {token}

Response (204): No Content
```

---

### Grades Endpoints

#### 1. Get All Grades
```
GET /api/grades
Authorization: Bearer {token}

Response (200):
[
  {
    "id": 1,
    "courseName": "Math 101",
    "grade": 95.5,
    "weight": 0.3
  }
]
```

#### 2. Get Grade by ID
```
GET /api/grades/{id}
Authorization: Bearer {token}
```

#### 3. Create Grade
```
POST /api/grades
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
  "courseName": "Math 101",
  "grade": 95.5,
  "weight": 0.3
}

Response (201): Returns created GradeDto
```

#### 4. Update Grade
```
PUT /api/grades/{id}
Authorization: Bearer {token}
Content-Type: application/json

Request Body: GradeDto object

Response (204): No Content
```

#### 5. Delete Grade
```
DELETE /api/grades/{id}
Authorization: Bearer {token}

Response (204): No Content
```

---

### Notes Endpoints

#### 1. Get All Notes
```
GET /api/notes
Authorization: Bearer {token}

Response (200):
[
  {
    "id": 1,
    "title": "Physics Chapter 5",
    "content": "Important concepts about motion",
    "createdAt": "2024-07-06T10:30:00Z"
  }
]
```

#### 2. Get Note by ID
```
GET /api/notes/{id}
Authorization: Bearer {token}
```

#### 3. Create Note
```
POST /api/notes
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
  "title": "Physics Chapter 5",
  "content": "Important concepts about motion",
  "createdAt": "2024-07-06T10:30:00Z"
}

Response (201): Returns created NoteDto
```

#### 4. Update Note
```
PUT /api/notes/{id}
Authorization: Bearer {token}
Content-Type: application/json

Request Body: NoteDto object

Response (204): No Content
```

#### 5. Delete Note
```
DELETE /api/notes/{id}
Authorization: Bearer {token}

Response (204): No Content
```

---

### Resources Endpoints

#### 1. Get All Resources
```
GET /api/resources
Authorization: Bearer {token}

Response (200):
[
  {
    "id": 1,
    "title": "Calculus Textbook",
    "url": "https://example.com/calculus.pdf",
    "category": "Textbook"
  }
]
```

#### 2. Get Resource by ID
```
GET /api/resources/{id}
Authorization: Bearer {token}
```

#### 3. Create Resource
```
POST /api/resources
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
  "title": "Calculus Textbook",
  "url": "https://example.com/calculus.pdf",
  "category": "Textbook"
}

Response (201): Returns created ResourceDto
```

#### 4. Update Resource
```
PUT /api/resources/{id}
Authorization: Bearer {token}
Content-Type: application/json

Request Body: ResourceDto object

Response (204): No Content
```

#### 5. Delete Resource
```
DELETE /api/resources/{id}
Authorization: Bearer {token}

Response (204): No Content
```

---

### Transactions Endpoints

#### 1. Get All Transactions
```
GET /api/transactions
Authorization: Bearer {token}

Response (200):
[
  {
    "id": 1,
    "description": "Textbook purchase",
    "amount": 45.99,
    "type": "EXPENSE",
    "date": "2024-07-01"
  }
]
```

#### 2. Get Transaction by ID
```
GET /api/transactions/{id}
Authorization: Bearer {token}
```

#### 3. Create Transaction
```
POST /api/transactions
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
  "description": "Textbook purchase",
  "amount": 45.99,
  "type": "EXPENSE",
  "date": "2024-07-01"
}

Response (201): Returns created TransactionDto
```

#### 4. Update Transaction
```
PUT /api/transactions/{id}
Authorization: Bearer {token}
Content-Type: application/json

Request Body: TransactionDto object

Response (204): No Content
```

#### 5. Delete Transaction
```
DELETE /api/transactions/{id}
Authorization: Bearer {token}

Response (204): No Content
```

---

## Authentication

All endpoints (except registration and login) require authentication.

### Token Format
```
Authorization: Bearer <jwt_token>
```

The token is obtained from the `/api/auth/register` or `/api/auth/login` endpoints.

### Error Responses

**400 Bad Request**
```json
{
  "message": "Invalid input"
}
```

**401 Unauthorized**
```json
{
  "message": "Invalid token or not authenticated"
}
```

**404 Not Found**
```json
{
  "message": "Resource not found"
}
```

**409 Conflict** (Registration)
```json
{
  "message": "Email already exists"
}
```

---

## Android Implementation Examples

### In StudyAppApiService.kt
All API calls are defined using Retrofit annotations. Example:

```kotlin
@POST("api/auth/login")
suspend fun login(@Body request: LoginDto): AuthResponseDto

@GET("api/tasks")
suspend fun getTasks(@Header("Authorization") token: String): List<TaskDto>

@PUT("api/tasks/{id}")
suspend fun updateTask(
    @Header("Authorization") token: String,
    @Path("id") id: Int,
    @Body task: TaskDto
): Unit
```

### In SyncedStudyRepository.kt
Repository methods handle both local and backend operations:

```kotlin
suspend fun login(email: String, password: String): AuthResponseDto {
    val response = apiService.login(LoginDto(email, password))
    authToken = response.token
    return response
}

suspend fun insertTask(task: Task) {
    taskDao.insertTask(task)
    val createTaskDto = CreateTaskDto(...)
    val remoteTask = apiService.createTask("Bearer $authToken", createTaskDto)
    taskDao.updateTask(task.copy(id = remoteTask.id))
}
```

---

## Common Issues

### 401 Unauthorized
- Token has expired or is invalid
- Token not included in Authorization header
- Token format incorrect (use "Bearer <token>")

### 404 Not Found
- Resource ID doesn't exist
- Typo in endpoint path

### 400 Bad Request
- Missing required fields
- Invalid data types
- Malformed JSON

### Connection Issues
- Backend not running
- Wrong backend URL
- Firewall blocking connection
