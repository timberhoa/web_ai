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
public class StudentAttendanceHistoryResponse {
    UUID attendanceId;
    UUID sessionId;
    UUID courseId;
    String courseName;
    String courseCode;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String roomName;
    Attendance.Status status;
    LocalDateTime checkedAt;
    String note;
}

