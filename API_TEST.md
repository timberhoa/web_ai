# API Test Documentation

## Endpoint: GET /api/user/students

### Mô tả
API để xem danh sách tất cả học sinh trong hệ thống. Chỉ có role ADMIN và TEACHER mới có thể truy cập endpoint này.

### Quyền truy cập
- **ADMIN**: Có thể xem tất cả học sinh
- **TEACHER**: Có thể xem tất cả học sinh  
- **STUDENT**: Không có quyền truy cập (403 Forbidden)

### Request
```http
GET /api/user/students
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### Response
#### Success (200 OK)
```json
[
  {
    "id": "uuid-string",
    "fullName": "Nguyen Van A",
    "username": "studentA",
    "email": "a@student.com",
    "phone": "0901111111",
    "role": "STUDENT",
    "active": true,
    "faculty": {
      "id": "faculty-uuid",
      "code": "IT",
      "name": "Information Technology",
      "description": "IT Faculty description",
      "head": null
    }
  },
  {
    "id": "uuid-string-2",
    "fullName": "Tran Thi B", 
    "username": "studentB",
    "email": "b@student.com",
    "phone": "0902222222",
    "role": "STUDENT",
    "active": true,
    "faculty": {
      "id": "faculty-uuid",
      "code": "IT",
      "name": "Information Technology", 
      "description": "IT Faculty description",
      "head": null
    }
  }
]
```

#### Error (403 Forbidden)
```json
{
  "error": "Access Denied",
  "message": "User does not have required role"
}
```

#### Error (401 Unauthorized)
```json
{
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

### Test Cases

#### Test Case 1: ADMIN Role Access
1. Login with ADMIN credentials
2. Make GET request to `/api/user/students`
3. Expected: 200 OK with list of all students

#### Test Case 2: TEACHER Role Access  
1. Login with TEACHER credentials
2. Make GET request to `/api/user/students`
3. Expected: 200 OK with list of all students

#### Test Case 3: STUDENT Role Access
1. Login with STUDENT credentials
2. Make GET request to `/api/user/students`
3. Expected: 403 Forbidden

#### Test Case 4: No Authentication
1. Make GET request to `/api/user/students` without Authorization header
2. Expected: 401 Unauthorized

### Implementation Details

#### Controller Method
```java
@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
@GetMapping("/students")
public ResponseEntity<List<UserResponse>> getAllStudents() {
    List<UserResponse> students = userService.getAllStudents();
    return ResponseEntity.ok(students);
}
```

#### Service Method
```java
public List<UserResponse> getAllStudents() {
    List<User> students = userRepository.findByRole(Role.STUDENT);
    
    return students.stream()
            .map(student -> UserResponse.builder()
                    .id(student.getId())
                    .fullName(student.getFullName())
                    .username(student.getUsername())
                    .email(student.getEmail())
                    .phone(student.getPhone())
                    .role(student.getRole())
                    .active(student.isActive())
                    .faculty(student.getFaculty())
                    .build())
            .collect(Collectors.toList());
}
```

### Notes
- Endpoint trả về tất cả học sinh không phân trang. Có thể cần thêm pagination cho hệ thống lớn
- Faculty information được include trong response để cung cấp context về khoa của học sinh
- Active status được bao gồm để biết học sinh có đang active hay không