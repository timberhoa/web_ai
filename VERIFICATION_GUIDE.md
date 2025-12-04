# ✅ Hướng Dẫn Kiểm Thử (Verification Guide)

Đã hoàn thành việc code Backend! Dưới đây là các bước để bạn kiểm thử tính năng Face Recognition.

## 1. Chuẩn Bị
- Đảm bảo **Colab API** đang chạy và đã update URL vào `application.properties`.
- Đảm bảo **Spring Boot App** đã khởi động lại.
- Đảm bảo **Database** đã có các cột mới (đã chạy migration).

## 2. Test Flow Đăng Ký (Admin)
Sử dụng Postman hoặc Swagger (`/swagger-ui/index.html`).

**Endpoint:** `POST /api/admin/user/{userId}/face/register`
- **Auth:** Login với tài khoản ADMIN.
- **Body (form-data):**
  - `images`: Chọn 3-5 file ảnh khuôn mặt của sinh viên.
- **Expected Result:**
  - Status: `200 OK`
  - Body: `{"success": true, "message": "Face registered successfully", ...}`
  - Database: Cột `face_vector` của user đó có dữ liệu JSON.

## 3. Test Check-in Hybrid (Student)
**Endpoint:** `POST /api/attendance/check-in-hybrid`
- **Auth:** Login với tài khoản STUDENT.
- **Body (form-data):**
  - `image`: 1 file ảnh selfie.
  - `sessionId`: ID của buổi học đang diễn ra.
  - `latitude`: Tọa độ GPS (ví dụ: `10.762622`).
  - `longitude`: Tọa độ GPS (ví dụ: `106.660172`).
- **Expected Result (Trường hợp tốt):**
  - Status: `200 OK`
  - Body: `{"success": true, "isMatch": true, "checkInType": "FACE_AND_LOCATION", ...}`

## 4. Test Fallback (Khi API Down)
- **Cách test:** Tắt Colab notebook hoặc đổi URL trong `application.properties` thành URL sai.
- **Thực hiện:** Gọi lại API Check-in Hybrid như bước 3.
- **Expected Result:**
  - Status: `200 OK` (Vẫn thành công!)
  - Body: `{"success": true, "checkInType": "LOCATION_ONLY (Fallback)", ...}`
  - Log console: Sẽ thấy warning `Face API down or error, falling back to location only`.

## 5. Test Validation
- Upload < 3 ảnh khi đăng ký -> Lỗi 400.
- Upload ảnh > 5MB -> Lỗi 400.
- Upload file không phải ảnh -> Lỗi 400.

---
**Lưu ý:** Nếu gặp lỗi `403 Forbidden`, kiểm tra lại Role của user và Token.
