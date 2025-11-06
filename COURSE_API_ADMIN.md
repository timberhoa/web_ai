# Course Management API - ADMIN Only

## Get All Courses (Admin Only)

### Endpoint
```
GET /api/course/admin/all
```

### Description
Lấy danh sách tất cả các course trong hệ thống. Chỉ có role ADMIN mới có thể truy cập endpoint này.

### Authorization
- **Required:** Bearer Token
- **Role:** ADMIN only

### Request Headers
```
Authorization: Bearer <your-jwt-token>
Content-Type: application/json
```

### Response

#### Success Response (200 OK)
```json
[
  {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "code": "CS101",
    "name": "Introduction to Computer Science",
    "teacher_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "teacher_name": "Dr. John Smith",
    "credits": 3,
    "faculty_code": "IT",
    "faculty_name": "Information Technology"
  },
  {
    "id": "4fa85f64-5717-4562-b3fc-2c963f66afa7",
    "code": "CS102",
    "name": "Data Structures and Algorithms",
    "teacher_id": "4fa85f64-5717-4562-b3fc-2c963f66afa7",
    "teacher_name": "Dr. Jane Doe",
    "credits": 4,
    "faculty_code": "IT",
    "faculty_name": "Information Technology"
  }
]
```

#### Error Responses

**401 Unauthorized**
```json
{
  "error": "Unauthorized",
  "message": "Access token is missing or invalid"
}
```

**403 Forbidden**
```json
{
  "error": "Forbidden",
  "message": "Access denied. Admin role required."
}
```

### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Unique identifier của course |
| `code` | String | Mã code của course (unique) |
| `name` | String | Tên của course |
| `teacher_id` | UUID | ID của giáo viên phụ tr책 (có thể null) |
| `teacher_name` | String | Tên của giáo viên phụ trách (có thể null) |
| `credits` | Integer | Số tín chỉ của course |
| `faculty_code` | String | Mã khoa |
| `faculty_name` | String | Tên khoa |

### Example Usage

#### cURL
```bash
curl -X GET "http://localhost:8080/api/course/admin/all" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json"
```

#### JavaScript (Fetch)
```javascript
fetch('http://localhost:8080/api/course/admin/all', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer your-jwt-token',
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

### Notes
- Endpoint này chỉ trả về thông tin cơ bản của course để tránh circular reference
- Thông tin teacher và faculty được flatten thành các trường riêng biệt
- Nếu course chưa có teacher được assign, `teacher_id` và `teacher_name` sẽ là `null`
- Response sẽ chứa tất cả course trong hệ thống, không có pagination (có thể cần bổ sung sau)