# API Documentation - Web AI Learning Management System

## Overview
This document describes the API endpoints for the Web AI Learning Management System. The system uses JWT authentication with roles: ADMIN, TEACHER, STUDENT.

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
