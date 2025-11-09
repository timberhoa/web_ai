package com.example.web_ai.dto.response;

import com.example.web_ai.entity.Attendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AttendanceRecordResponse {
    UUID attendanceId;
    UUID studentId;
    String studentName;
    Attendance.Status status;
    LocalDateTime checkedAt;
    Double studentLat;
    Double studentLng;
    String note;
}
