# API Documentation - Web AI Learning Management System

## Overview
This document describes the API endpoints for the Web AI Learning Management System. The system uses JWT authentication with roles: ADMIN, TEACHER, STUDENT.

- Can using through Swagger though:
http://localhost:8080/swagger-ui/index.html

---

## 1. AuthController - Authentication
**Base URL:** `/api/auth`

### 1.1 Register User
- **Endpoint:** `POST /api/auth/register`
- **Description:** Create a new user account
- **Authentication:** Not required
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "fullName": "string (required)",
  "username": "string (required)",
  "password": "string (required)",
  "email": "string (required, email format)",
  "phone": "string (required)",
  "role": "ADMIN | TEACHER | STUDENT",
  "active": "boolean (required)",
  "facultyId": "UUID (optional, null for admin)"
}
```

**Response (200 OK):**
```json
{
  "id": "UUID",
  "fullName": "string",
  "username": "string", 
  "email": "string",
  "phone": "string",
  "role": "ADMIN | TEACHER | STUDENT",
  "active": "boolean",
  "faculty": {
    "id": "UUID",
    "name": "string",
    "description": "string"
  }
}
```

### 1.2 Login
- **Endpoint:** `POST /api/auth/login`
- **Description:** Authenticate user and return JWT token
- **Authentication:** Not required
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "username": "string (required)",
  "password": "string (required)"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "string (JWT token)",
  "tokenType": "Bearer"
}
```

### 1.3 Logout
- **Endpoint:** `POST /api/auth/logout`
- **Description:** Logout user
- **Authentication:** Not required
- **Content-Type:** `application/json`

**Response (200 OK):**
```json
"Logout successful"
```

---

## 2. UserController - User Management
**Base URL:** `/api/user`

### 2.1 Get User By ID
- **Endpoint:** `GET /api/user/getUserById?id={userId}`
- **Description:** Get detailed information of a specific user
- **Authentication:** JWT Token required
- **Roles:** ADMIN, TEACHER
- **Parameters:** `id` (UUID, required) - User ID

**Response (200 OK):**
```json
{
  "id": "UUID",
  "fullName": "string",
  "username": "string",
  "email": "string", 
  "phone": "string",
  "role": "ADMIN | TEACHER | STUDENT",
  "active": "boolean",
  "faculty": {
    "id": "UUID",
    "name": "string",
    "description": "string"
  }
}
```

### 2.2 Get My Profile
- **Endpoint:** `GET /api/user/me`
- **Description:** Get personal information of current user
- **Authentication:** JWT Token required
- **Roles:** TEACHER

**Response (200 OK):**
```json
{
  "id": "UUID",
  "fullName": "string",
  "username": "string",
  "email": "string",
  "phone": "string", 
  "role": "TEACHER",
  "active": "boolean",
  "faculty": {
    "id": "UUID",
    "name": "string",
    "description": "string"
  }
}
```

### 2.3 Update My Profile
- **Endpoint:** `PUT /api/user/me`
- **Description:** Update personal information of current user
- **Authentication:** JWT Token required
- **Roles:** TEACHER
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "fullName": "string (required)",
  "username": "string (required)",
  "email": "string (required, email format)",
  "phone": "string (required)"
}
```

**Response (200 OK):**
```json
{
  "id": "UUID",
  "fullName": "string",
  "username": "string",
  "email": "string",
  "phone": "string",
  "role": "TEACHER",
  "active": "boolean",
  "faculty": {
    "id": "UUID", 
    "name": "string",
    "description": "string"
  }
}
```

### 2.4 Update User (Admin)
- **Endpoint:** `PUT /api/user/{id}`
- **Description:** Update information of any user
- **Authentication:** JWT Token required
- **Roles:** ADMIN
- **Parameters:** `id` (UUID, required) - User ID
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "fullName": "string (required)",
  "username": "string (required)",
  "password": "string (required)",
  "email": "string (required, email format)",
  "phone": "string (required)",
  "role": "ADMIN | TEACHER | STUDENT",
  "active": "boolean (required)",
  "facultyId": "UUID (optional)"
}
```

**Response (200 OK):**
```json
{
  "id": "UUID",
  "fullName": "string",
  "username": "string",
  "email": "string",
  "phone": "string",
  "role": "ADMIN | TEACHER | STUDENT", 
  "active": "boolean",
  "faculty": {
    "id": "UUID",
    "name": "string",
    "description": "string"
  }
}
```

### 2.5 Update Password
- **Endpoint:** `PUT /api/user/updatePassword/{id}`
- **Description:** Change user password
- **Authentication:** JWT Token required
- **Roles:** ADMIN, STUDENT, TEACHER
- **Parameters:** `id` (UUID, required) - User ID
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "oldPassword": "string",
  "newPassword": "string", 
  "confirmPassword": "string"
}
```

**Response (200 OK):**
```json
"Password updated successfully"
```

### 2.6 Get Student Grades
- **Endpoint:** `GET /api/user/grades`
- **Description:** Get all grades of current student
- **Authentication:** JWT Token required
- **Roles:** STUDENT

**Response (200 OK):**
```json
{
  "studentId": "UUID",
  "studentName": "string",
  "grades": [
    {
      "courseId": "UUID",
      "courseName": "string",
      "grade": "number",
      "gradeDate": "datetime"
    }
  ]
}
```

### 2.7 Get Image By ID
- **Endpoint:** `GET /api/user/image/{imageId}`
- **Description:** Get image information by ID
- **Authentication:** Not required
- **Parameters:** `imageId` (UUID, required) - Image ID

**Response (200 OK):**
```json
{
  "id": "UUID",
  "fileName": "string",
  "fileType": "string",
  "data": "byte[]"
}
```

### 2.8 Upload Image
- **Endpoint:** `POST /api/user/image`
- **Description:** Upload profile image for user
- **Authentication:** JWT Token required
- **Content-Type:** `multipart/form-data`
- **File constraints:** 
  - Only accepts image files (image/*)
  - Maximum size: 5MB

**Request Body:**
```
file: MultipartFile (required)
```

**Response (200 OK):**
```json
{
  "message": "Upload successfully",
  "id": "UUID"
}
```

**Response (400 Bad Request):**
```json
{
  "message": "File is empty | Content type not allowed | File exceeds 5MP | Failed to upload image",
  "id": "UUID"
}
```

### 2.9 Get All Students (Paginated)
- **Endpoint:** `GET /api/user/students`
- **Description:** Get list of all students with pagination
- **Authentication:** JWT Token required
- **Roles:** ADMIN, TEACHER
- **Query Parameters:**
  - `page` (int, optional, default: 0) - Page number (starts from 0)
  - `size` (int, optional, default: 15) - Number of records per page
  - `sort` (string, optional, default: "fullName") - Sort field

**Example:** `GET /api/user/students?page=0&size=10&sort=fullName`

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "UUID",
      "fullName": "string",
      "username": "string",
      "email": "string",
      "phone": "string",
      "role": "STUDENT",
      "active": "boolean",
      "faculty": {
        "id": "UUID",
        "name": "string",
        "description": "string"
      }
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 15,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 100,
  "totalPages": 7,
  "last": false,
  "first": true,
  "numberOfElements": 15,
  "size": 15,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false
  }
}
```

### 2.10 Search Users (Paginated)
- **Endpoint:** `GET /api/user/search`
- **Description:** Search users by name, username, email or phone
- **Authentication:** JWT Token required
- **Roles:** ADMIN, TEACHER
- **Query Parameters:**
  - `q` (string, required) - Search keyword
  - `role` (string, optional) - Filter by role (ADMIN, TEACHER, STUDENT)
  - `page` (int, optional, default: 0) - Page number
  - `size` (int, optional, default: 10) - Number of records per page
  - `sort` (string, optional, default: "fullName") - Sort field

**Example:** `GET /api/user/search?q=john&role=STUDENT&page=0&size=5`

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "UUID",
      "fullName": "string",
      "username": "string", 
      "email": "string",
      "phone": "string",
      "role": "STUDENT",
      "active": "boolean",
      "faculty": {
        "id": "UUID",
        "name": "string",
        "description": "string"
      }
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 25,
  "totalPages": 3, 
  "last": false,
  "first": true,
  "numberOfElements": 10,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false
  }
}
```

---

## 3. AdminController - Administration
**Base URL:** `/api/admin`
**Global Role Required:** ADMIN (all endpoints require ADMIN role)

### 3.1 Get All Users (Paginated)
- **Endpoint:** `GET /api/admin/users`
- **Description:** Get list of all users with pagination
- **Authentication:** JWT Token required
- **Roles:** ADMIN
- **Query Parameters:**
  - `page` (int, optional, default: 0) - Page number
  - `size` (int, optional, default: 10) - Number of records per page
  - `sort` (string, optional, default: "fullName") - Sort field

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "UUID",
      "fullName": "string",
      "username": "string",
      "email": "string",
      "phone": "string",
      "role": "ADMIN | TEACHER | STUDENT",
      "active": "boolean",
      "faculty": {
        "id": "UUID",
        "name": "string",
        "description": "string"
      }
    }
  ],
  "pageable": "...",
  "totalElements": "number",
  "totalPages": "number"
}
```

### 3.2 Filter Users by Role (Paginated)
- **Endpoint:** `GET /api/admin/users/filter?role={role}`
- **Description:** Filter users by specific role with pagination
- **Authentication:** JWT Token required
- **Roles:** ADMIN
- **Query Parameters:**
  - `role` (string, required) - Role to filter (ADMIN, TEACHER, STUDENT)
  - `page` (int, optional, default: 0) - Page number
  - `size` (int, optional, default: 10) - Number of records per page
  - `sort` (string, optional, default: "fullName") - Sort field

**Example:** `GET /api/admin/users/filter?role=TEACHER&page=0&size=5`

**Response (200 OK):** Same structure as Get All Users

### 3.3 Add User
- **Endpoint:** `POST /api/admin/addUser`
- **Description:** Create a new user account
- **Authentication:** JWT Token required
- **Roles:** ADMIN
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "fullName": "string (required)",
  "username": "string (required)",
  "password": "string (required)",
  "email": "string (required, email format)",
  "phone": "string (required)",
  "role": "ADMIN | TEACHER | STUDENT",
  "active": "boolean (required)",
  "facultyId": "UUID (optional)"
}
```

**Response (200 OK):**
```json
{
  "id": "UUID",
  "fullName": "string",
  "username": "string",
  "email": "string",
  "phone": "string",
  "role": "ADMIN | TEACHER | STUDENT",
  "active": "boolean",
  "faculty": {
    "id": "UUID",
    "name": "string",
    "description": "string"
  }
}
```

### 3.4 Delete User
- **Endpoint:** `DELETE /api/admin/users/{id}`
- **Description:** Delete a user account
- **Authentication:** JWT Token required
- **Roles:** ADMIN
- **Parameters:** `id` (UUID, required) - User ID

**Response (204 No Content):** Empty body

### 3.5 Update User
- **Endpoint:** `PUT /api/admin/users/{id}`
- **Description:** Update user information
- **Authentication:** JWT Token required
- **Roles:** ADMIN
- **Parameters:** `id` (UUID, required) - User ID
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "fullName": "string (required)",
  "username": "string (required)",
  "password": "string (required)",
  "email": "string (required, email format)",
  "phone": "string (required)",
  "role": "ADMIN | TEACHER | STUDENT",
  "active": "boolean (required)",
  "facultyId": "UUID (optional)"
}
```

**Response (200 OK):** Same structure as Add User

### 3.6 Get Faculty Student Statistics
- **Endpoint:** `GET /api/admin/stats/faculty-students`
- **Description:** Get statistics of students per faculty
- **Authentication:** JWT Token required
- **Roles:** ADMIN

**Response (200 OK):**
```json
[
  {
    "facultyId": "UUID",
    "facultyName": "string",
    "facultyCode": "string",
    "studentCount": "number",
    "activeStudents": "number",
    "inactiveStudents": "number"
  }
]
```

### 3.7 Get Attendance Statistics by Session
- **Endpoint:** `GET /api/admin/stats/attendance/{sessionId}`
- **Description:** Get attendance statistics for a specific class session
- **Authentication:** JWT Token required
- **Roles:** ADMIN
- **Parameters:** `sessionId` (UUID, required) - Class session ID

**Response (200 OK):**
```json
{
  "sessionId": "UUID",
  "courseName": "string",
  "sessionDate": "datetime",
  "totalStudents": "number",
  "presentStudents": "number",
  "absentStudents": "number",
  "lateStudents": "number",
  "attendanceRate": "number"
}
```

### 3.8 Get All Class Sessions (Paginated)
- **Endpoint:** `GET /api/admin/sessions`
- **Description:** Get list of all class sessions with pagination
- **Authentication:** JWT Token required
- **Roles:** ADMIN
- **Query Parameters:**
  - `page` (int, optional, default: 0) - Page number
  - `size` (int, optional, default: 10) - Number of records per page
  - `sort` (string, optional, default: "startTime") - Sort field

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "UUID",
      "courseId": "UUID",
      "courseName": "string",
      "startTime": "datetime",
      "endTime": "datetime",
      "location": "string",
      "description": "string"
    }
  ],
  "pageable": "...",
  "totalElements": "number",
  "totalPages": "number"
}
```

---

## 4. CourseController - Course Management
**Base URL:** `/api/course`

### 4.1 Create Course
- **Endpoint:** `POST /api/course/`
- **Description:** Create a new course
- **Authentication:** JWT Token required
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "code": "string",
  "name": "string",
  "teacher_id": "UUID",
  "credits": "number",
  "faculty_id": "UUID"
}
```

**Response (200 OK):**
```json
{
  "id": "UUID",
  "code": "string",
  "name": "string",
  "credits": "number",
  "teacher": {
    "id": "UUID",
    "fullName": "string",
    "username": "string",
    "email": "string"
  },
  "faculty": {
    "id": "UUID",
    "name": "string",
    "code": "string"
  }
}
```

### 4.2 Update Course
- **Endpoint:** `PUT /api/course/{id}`
- **Description:** Update course information
- **Authentication:** JWT Token required
- **Parameters:** `id` (UUID, required) - Course ID
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "code": "string",
  "name": "string",
  "teacher_id": "UUID",
  "credits": "number",
  "faculty_id": "UUID"
}
```

**Response (200 OK):** Same structure as Create Course

### 4.3 Delete Course
- **Endpoint:** `DELETE /api/course/{id}`
- **Description:** Delete a course
- **Authentication:** JWT Token required
- **Parameters:** `id` (UUID, required) - Course ID

**Response (204 No Content):** Empty body

### 4.4 Get Course by ID
- **Endpoint:** `GET /api/course/{id}`
- **Description:** Get detailed information of a specific course
- **Authentication:** JWT Token required
- **Parameters:** `id` (UUID, required) - Course ID

**Response (200 OK):** Same structure as Create Course

### 4.5 List Courses (Paginated)
- **Endpoint:** `GET /api/course`
- **Description:** Get list of courses with pagination
- **Authentication:** JWT Token required
- **Query Parameters:**
  - `page` (int, optional, default: 0) - Page number
  - `size` (int, optional, default: 10) - Number of records per page
  - `sort` (string, optional, default: "name") - Sort field

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "UUID",
      "code": "string",
      "name": "string",
      "credits": "number",
      "teacher": {
        "id": "UUID",
        "fullName": "string",
        "username": "string",
        "email": "string"
      },
      "faculty": {
        "id": "UUID",
        "name": "string",
        "code": "string"
      }
    }
  ],
  "pageable": "...",
  "totalElements": "number",
  "totalPages": "number"
}
```

### 4.6 Get Courses by Faculty Code (Paginated)
- **Endpoint:** `GET /api/course/by-faculty/{faculty_code}`
- **Description:** Get courses by faculty code with pagination
- **Authentication:** JWT Token required
- **Parameters:** `faculty_code` (string, required) - Faculty code
- **Query Parameters:**
  - `page` (int, optional, default: 0) - Page number
  - `size` (int, optional, default: 10) - Number of records per page
  - `sort` (string, optional, default: "name") - Sort field

**Response (200 OK):** Same structure as List Courses

### 4.7 Get All Courses for Admin (Paginated)
- **Endpoint:** `GET /api/course/admin/all`
- **Description:** Get all courses for admin view with pagination
- **Authentication:** JWT Token required
- **Query Parameters:**
  - `page` (int, optional, default: 0) - Page number
  - `size` (int, optional, default: 15) - Number of records per page
  - `sort` (string, optional, default: "name") - Sort field

**Response (200 OK):** Same structure as List Courses

### 4.8 Search Courses by Name (Paginated)
- **Endpoint:** `GET /api/course/search?name={searchTerm}`
- **Description:** Search courses by name with pagination
- **Authentication:** JWT Token required
- **Query Parameters:**
  - `name` (string, required) - Search term for course name
  - `page` (int, optional, default: 0) - Page number
  - `size` (int, optional, default: 10) - Number of records per page
  - `sort` (string, optional, default: "name") - Sort field

**Example:** `GET /api/course/search?name=java&page=0&size=5`

**Response (200 OK):** Same structure as List Courses

---

## 5. EnrollmentController - Student Enrollments
**Base URL:** `/api/enrollment`

### 5.1 Get Student's Enrolled Courses (Paginated)
- **Endpoint:** `GET /api/enrollment/courses`
- **Description:** Get all courses enrolled by current student with pagination
- **Authentication:** JWT Token required
- **Roles:** STUDENT
- **Query Parameters:**
  - `page` (int, optional, default: 0) - Page number
  - `size` (int, optional, default: 10) - Number of records per page
  - `sort` (string, optional, default: "name") - Sort field

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "UUID",
      "code": "string",
      "name": "string",
      "credits": "number",
      "teacher": {
        "id": "UUID",
        "fullName": "string",
        "username": "string",
        "email": "string"
      },
      "faculty": {
        "id": "UUID",
        "name": "string",
        "code": "string"
      }
    }
  ],
  "pageable": "...",
  "totalElements": "number",
  "totalPages": "number"
}
```

---

## 6. FacultyController - Faculty Management
**Base URL:** `/api/faculties`

### 6.1 Add Faculty
- **Endpoint:** `POST /api/faculties`
- **Description:** Create a new faculty
- **Authentication:** JWT Token required
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "code": "string",
  "name": "string"
}
```

**Response (200 OK):**
```json
{
  "id": "UUID",
  "code": "string",
  "name": "string",
  "description": "string"
}
```

### 6.2 Update Faculty
- **Endpoint:** `PUT /api/faculties/{id}`
- **Description:** Update faculty information
- **Authentication:** JWT Token required
- **Parameters:** `id` (UUID, required) - Faculty ID
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "code": "string",
  "name": "string"
}
```

**Response (200 OK):** Same structure as Add Faculty

### 6.3 Delete Faculty
- **Endpoint:** `DELETE /api/faculties/{id}`
- **Description:** Delete a faculty
- **Authentication:** JWT Token required
- **Parameters:** `id` (UUID, required) - Faculty ID

**Response (204 No Content):** Empty body

### 6.4 Get Faculty by ID
- **Endpoint:** `GET /api/faculties/{id}`
- **Description:** Get detailed information of a specific faculty
- **Authentication:** JWT Token required
- **Parameters:** `id` (UUID, required) - Faculty ID

**Response (200 OK):** Same structure as Add Faculty

### 6.5 List All Faculties
- **Endpoint:** `GET /api/faculties`
- **Description:** Get list of all faculties
- **Authentication:** JWT Token required

**Response (200 OK):**
```json
[
  {
    "id": "UUID",
    "code": "string",
    "name": "string",
    "description": "string"
  }
]
```

---

## 7. AttendanceController - Attendance Management
**Base URL:** `/api/attendance`

### 7.1 Check Attendance
- **Endpoint:** `POST /api/attendance`
- **Description:** Record student attendance for a class session
- **Authentication:** JWT Token required
- **Content-Type:** `application/json`

**Request Body:**
```json
{
  "status": "PRESENT | ABSENT | LATE",
  "student_id": "UUID",
  "session_id": "UUID",
  "studentLat": "number (latitude)",
  "studentLng": "number (longitude)"
}
```

**Response (200 OK):**
```json
{
  "id": "UUID",
  "studentId": "UUID",
  "studentName": "string",
  "sessionId": "UUID",
  "status": "PRESENT | ABSENT | LATE",
  "checkTime": "datetime",
  "latitude": "number",
  "longitude": "number"
}
```

---

## Common HTTP Status Codes

- **200 OK:** Request successful
- **201 Created:** Resource created successfully
- **204 No Content:** Request successful, no content returned
- **400 Bad Request:** Invalid input data
- **401 Unauthorized:** Not authenticated or invalid token
- **403 Forbidden:** No permission to access resource
- **404 Not Found:** Resource not found
- **500 Internal Server Error:** Server error

## Authentication Headers
For endpoints requiring authentication, include JWT token in header:
```
Authorization: Bearer <jwt_token>
```

## Pagination Parameters (Common for all paginated endpoints)
- `page` (int, optional, default: 0) - Page number (starts from 0)
- `size` (int, optional) - Number of records per page (varies by endpoint)
- `sort` (string, optional) - Sort field (varies by endpoint)

## Standard Pagination Response Structure
```json
{
  "content": "array of objects",
  "pageable": {
    "sort": {
      "sorted": "boolean",
      "unsorted": "boolean"
    },
    "pageNumber": "number",
    "pageSize": "number",
    "offset": "number",
    "paged": "boolean",
    "unpaged": "boolean"
  },
  "totalElements": "number",
  "totalPages": "number",
  "last": "boolean",
  "first": "boolean",
  "numberOfElements": "number",
  "size": "number",
  "number": "number",
  "sort": {
    "sorted": "boolean",
    "unsorted": "boolean"
  }
}
```

---

## 5. ScheduleController – Class Session Management
**Base URL:** `/api/schedule`

### 5.1 Create Single Session
- **Endpoint:** `POST /api/schedule`
- **Description:** Create one class occurrence with room and optional geo-fence. (Roles: ADMIN, TEACHER)

**Request Body**
```json
{
  "courseId": "UUID",
  "startTime": "2025-01-20T08:00:00",
  "endTime": "2025-01-20T10:00:00",
  "roomName": "A1.201",
  "latitude": 10.762622,
  "longitude": 106.660172,
  "radiusMeters": 30
}
```

### 5.2 Generate Recurring Sessions
- **Endpoint:** `POST /api/schedule/recurring`
- **Description:** Generate multiple sessions between two dates for selected weekdays. (Roles: ADMIN, TEACHER)

**Request Body**
```json
{
  "courseId": "UUID",
  "startDate": "2025-01-20",
  "endDate": "2025-03-30",
  "startTime": "08:00:00",
  "endTime": "10:00:00",
  "daysOfWeek": ["MONDAY", "WEDNESDAY"],
  "roomName": "A1.201"
}
```

### 5.3 Update/Delete/Get/Search Sessions
- `PUT /api/schedule/{sessionId}` – Update time or room (blocked when locked)
- `DELETE /api/schedule/{sessionId}` – Remove session (blocked when locked)
- `GET /api/schedule/{sessionId}` – Session detail
- `GET /api/schedule?courseId=&teacherId=&from=&to=` – Filter with pagination

**Response Example**
```json
{
  "sessionId": "UUID",
  "courseId": "UUID",
  "courseName": "AI 101",
  "courseCode": "AI101",
  "teacherId": "UUID",
  "teacherName": "Dr. Smith",
  "roomName": "A1.201",
  "startTime": "2025-01-20T08:00:00",
  "endTime": "2025-01-20T10:00:00",
  "locked": false
}
```

---

## 6. AttendanceController – Capture, Monitor & Review
**Base URL:** `/api/attendance`

### 6.1 Check Attendance
- **Endpoint:** `POST /api/attendance`
- **Description:** Teacher confirms/updates a student attendance for a session.

**Request**
```json
{
  "session_id": "UUID",
  "student_id": "UUID",
  "status": "PRESENT",
  "studentLat": 10.7626,
  "studentLng": 106.6601,
  "note": "Front row"
}
```

**Response**
```json
{
  "message": "Check attendance successfully",
  "attendance_id": "UUID",
  "studentName": "Jane Doe"
}
```

### 6.2 Session Detail & Editing
- `GET /api/attendance/session/{sessionId}` – Returns session metadata, all records, and stats.
- `PUT /api/attendance/{attendanceId}` – Update status/location/note before the session is locked.

### 6.3 Live Monitoring Dashboard
- **Endpoint:** `GET /api/attendance/monitor`
- **Query Params:** `courseId`, `teacherId`, `minutesBefore` (default 15), `minutesAfter` (default 30)
- **Description:** Lists sessions happening within the window with aggregated counts.

### 6.4 Review & Close Sessions
- **Endpoint:** `GET /api/attendance/review?from=2025-01-01&to=2025-01-07`
- **Description:** Summaries for sessions in the range for auditing (filter by course/teacher as needed).
- **Lock/Unlock:** `POST /api/attendance/review/{sessionId}/lock` and `/unlock`

**Summary Response**
```json
{
  "sessionId": "UUID",
  "courseName": "AI 101",
  "roomName": "A1.201",
  "startTime": "2025-01-20T08:00:00",
  "endTime": "2025-01-20T10:00:00",
  "locked": false,
  "totalEnrolled": 40,
  "totalMarked": 35,
  "presentCount": 30,
  "lateCount": 3,
  "absentCount": 5,
  "excusedCount": 2
}
```

---

## 7. Course & Attendance Integration (Full)

This section consolidates all endpoints needed to integrate Course management with Attendance: create/update Course, enroll Students, set up Sessions, seed roster, marking, monitoring, and stats.

Authentication header for protected endpoints:
```
Authorization: Bearer <JWT>
Content-Type: application/json
```

### 7.1 High-level Flow
- Admin tạo Course và gán Teacher cho Course.
- Admin enroll Students vào Course.
- Admin/Teacher tạo Sessions (đơn lẻ/định kỳ) cho Course.
- (Tùy chọn) Seed attendance cho Session để tạo sẵn bản ghi ABSENT cho toàn bộ sinh viên đã enroll.
- Teacher/Admin dùng Roster của Session để chấm công (PRESENT/LATE/EXCUSED) hoặc giữ ABSENT.
- Theo dõi qua Monitor/Review; chốt (lock) khi hoàn thành; xem thống kê.

### 7.2 Courses
- Create Course — `POST /api/course/`
```json
{
  "code": "JAVA101",
  "name": "Lap trinh Java",
  "teacher_id": "UUID",
  "credits": 3,
  "faculty_id": "UUID"
}
```

- Update Course (assign/change teacher) — `PUT /api/course/{id}`
```json
{ "teacher_id": "UUID" }
```

- List (Admin) — `GET /api/course/admin/all?page=0&size=15&sort=name,asc`
- Search — `GET /api/course/search?name=java&page=0&size=10&sort=name,asc`

### 7.3 Enrollment (Admin)
- Bulk enroll Students — `POST /api/admin/enrollments`
```json
{
  "courseId": "UUID",
  "studentIds": ["UUID", "UUID"]
}
```
Response
```json
{
  "courseId": "UUID",
  "addedCount": 2,
  "skippedCount": 0,
  "addedStudentIds": ["UUID", "UUID"],
  "skippedStudentIds": []
}
```

- List students by Course — `GET /api/admin/enrollments?courseId=UUID`
```json
[
  {"enrollmentId":"UUID","studentId":"UUID","studentName":"A","studentEmail":"a@example.com"}
]
```

- Delete enrollment — `DELETE /api/admin/enrollments/{enrollmentId}`

- Seed enrollments — `POST /api/admin/enrollments/seed`
```json
{
  "courseId": "UUID",
  "facultyId": "UUID",
  "limit": 30
}
```
Response is the same structure as bulk enroll.

- Student: my Courses — `GET /api/enrollment/courses?page=0&size=10&sort=name` (Role: STUDENT)

### 7.4 Schedule (Timetable)
- Create Session — `POST /api/schedule`
```json
{
  "courseId": "UUID",
  "startTime": "2025-02-10T08:00:00",
  "endTime": "2025-02-10T10:00:00",
  "roomName": "D5-201",
  "latitude": 21.028511,
  "longitude": 105.804817,
  "radiusMeters": 30
}
```
Errors: `COURSE_NOT_FOUND`, `INVALID_TIME_RANGE`, `END_TIME_MUST_BE_AFTER_START_TIME`, `COURSE_SESSION_CONFLICT`, `ROOM_SESSION_CONFLICT`.

- Create Recurring — `POST /api/schedule/recurring`
```json
{
  "courseId": "UUID",
  "startDate": "2025-02-10",
  "endDate": "2025-05-10",
  "startTime": "08:00:00",
  "endTime": "10:00:00",
  "daysOfWeek": ["MONDAY","WEDNESDAY"],
  "roomName": "D5-201",
  "latitude": 21.028511,
  "longitude": 105.804817,
  "radiusMeters": 30
}
```

- Update — `PUT /api/schedule/{sessionId}`
- Delete — `DELETE /api/schedule/{sessionId}`
- Detail — `GET /api/schedule/{sessionId}`
- Search — `GET /api/schedule?courseId=UUID&teacherId=UUID&from=2025-02-01T00:00:00&to=2025-02-28T23:59:59&page=0&size=20&sort=startTime,asc`
- Admin list — `GET /api/admin/sessions?page=0&size=10&sort=startTime,desc`

### 7.5 Attendance
- Session roster — `GET /api/attendance/session/{sessionId}/roster`
```json
{
  "studentId": "UUID",
  "studentName": "string",
  "studentEmail": "string",
  "marked": true,
  "status": "PRESENT | LATE | ABSENT | EXCUSED | null",
  "checkedAt": "2025-02-10T08:30:00",
  "studentLat": 21.0285,
  "studentLng": 105.8048,
  "note": "string"
}
```

- Seed attendance — `POST /api/attendance/session/{sessionId}/seed`
```json
{
  "sessionId": "UUID",
  "totalEnrollments": 40,
  "createdCount": 40,
  "skippedCount": 0
}
```

- Check attendance — `POST /api/attendance`
```json
{
  "status": "PRESENT",
  "student_id": "UUID",
  "session_id": "UUID",
  "studentLat": 21.0285,
  "studentLng": 105.8048,
  "note": "Checked"
}
```

- Update attendance – `PUT /api/attendance/{attendanceId}`

- Session detail – `GET /api/attendance/session/{sessionId}` (records + stats)

- Monitor live — `GET /api/attendance/monitor?courseId=UUID&teacherId=UUID&minutesBefore=15&minutesAfter=15`
  - `totalMarked` chỉ tính PRESENT/LATE/EXCUSED (không tính ABSENT được seed).

- Review range — `GET /api/attendance/review?from=2025-02-01&to=2025-02-28&courseId=UUID&teacherId=UUID`

- Lock/Unlock – `POST /api/attendance/review/{sessionId}/lock` | `/unlock`

Teacher assisted confirmation (UI flow)
- From roster `GET /api/attendance/session/{sessionId}/roster`, render each student row with current status.
- On "Confirm" button, call `POST /api/attendance` with body `{ status: "PRESENT" | "LATE" | "EXCUSED", student_id, session_id, studentLat?, studentLng?, note? }`.
- Optionally seed first using `POST /api/attendance/session/{sessionId}/seed` so unmarked students show as `ABSENT`.

### 7.6 Admin Stats by Session
- `GET /api/admin/stats/attendance/{sessionId}` — tổng hợp tỉ lệ/đếm theo ca.

### 7.7 Notes
- Date/Time: ISO 8601. `daysOfWeek`: `MONDAY..SUNDAY`.
- Conflict check: trùng giờ theo Course và theo Room.
- Session `locked` chặn sửa attendance.
- Seed tạo ABSENT cho sinh viên chưa có record; ABSENT không tính là “marked”.

---

## 8. Student APIs (Self-Service Attendance)

All endpoints below require `ROLE_STUDENT` and JWT token.

### 8.0 My Courses
- **Endpoint:** `GET /api/student/courses`
- **Description:** Returns the list of `CourseResponse` that the authenticated student is enrolled in.

### 8.1 My Sessions
- **Endpoint:** `GET /api/student/sessions`
- **Query Params:** `from`, `to` (ISO `LocalDateTime`, optional), pagination params.
- **Description:** Returns paged `ClassSessionResponse` for sessions of courses the student enrolled in.

### 8.2 My Attendance History
- **Endpoint:** `GET /api/student/attendance/history`
- **Query Params:** `courseId` (optional), `from`, `to`, pagination.
- **Description:** Returns paged list of `StudentAttendanceHistoryResponse`.
```json
{
  "attendanceId": "UUID",
  "sessionId": "UUID",
  "courseId": "UUID",
  "courseName": "AI 101",
  "courseCode": "AI101",
  "startTime": "2025-02-10T08:00:00",
  "endTime": "2025-02-10T10:00:00",
  "roomName": "D5-201",
  "status": "PRESENT",
  "checkedAt": "2025-02-10T08:05:00",
  "note": "Arrived on time"
}
```

### 8.3 My Attendance Stats
- **Endpoint:** `GET /api/student/attendance/stats`
- **Query Params:** `courseId` (optional), `from`, `to`.
- **Description:** Aggregated stats per course (list).
```json
{
  "courseId": "UUID",
  "courseName": "AI 101",
  "courseCode": "AI101",
  "totalSessions": 15,
  "attendedSessions": 14,
  "presentCount": 12,
  "lateCount": 1,
  "excusedCount": 1,
  "absentCount": 1,
  "attendanceRate": 93.33
}
```

### 8.4 Self Check-in (Face/Geo)
- Endpoint: `POST /api/attendance/self`
- Roles: STUDENT
- Description: Student checks in for a specific session after face scan on client side. Validates time window and optional geo-fence.

Request
```json
{
  "session_id": "UUID",
  "studentLat": 21.0285,
  "studentLng": 105.8048,
  "imageId": "UUID (optional)"
}
```

Rules
- Valid only between `startTime - 15m` and `endTime + 15m` of the session.
- Requires enrollment in the course; else `NOT_ENROLLED`.
- If session has `radiusMeters > 0`, must provide `studentLat/studentLng` and be within radius (+20m). Errors: `LOCATION_REQUIRED`, `OUT_OF_GEOFENCE`.
- Status is auto-set: `PRESENT` if within 10m from start, otherwise `LATE`.
- Note is saved as `SELF_CHECK` and `checkedAt` is the server time.

Response (200)
```json
{ "message": "Self check-in successfully", "attendance_id": "UUID", "studentName": "John Doe" }
```

---

## 9. Dashboards

All endpoints require JWT.

### 9.1 Admin Dashboard
- Endpoint: `GET /api/dashboard/admin`
- Fields: `totalStudents`, `totalCourses`, `sessionsToday`, `checkinsToday`, `attendanceRate` (0-100).

### 9.2 Teacher Dashboard
- Endpoint: `GET /api/dashboard/teacher`
- Fields: `totalCourses`, `sessionsToday`, `totalCheckinsToday`, `absentToday`, `lateToday`, `upcomingSessions: ClassSessionResponse[]`.

### 9.3 Student Dashboard
- Endpoint: `GET /api/dashboard/student`
- Fields: `totalCourses`, `todayCheckins`, `faceRegistered`, `todaySessions: ClassSessionResponse[]`, `latestAttendance: StudentAttendanceHistoryResponse|null`.

FE Mapping (screens in images)
- Admin: map top cards to fields above; warning widgets can remain placeholder for now.
- Teacher: top KPI cards map to counts; bottom sections use monitor/review endpoints for drill-down.
- Student: "Lịch học hôm nay" = `todaySessions`; "Trạng thái điểm danh gần nhất" = `latestAttendance`; face warning uses `faceRegistered`.
