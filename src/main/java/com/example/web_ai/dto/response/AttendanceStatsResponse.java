package com.example.web_ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AttendanceStatsResponse {
    UUID sessionId;
    String courseName;
    String courseCode;
    String roomName;
    Long totalEnrolledStudents;
    Long presentStudents;
    Long lateStudents;
    Long absentStudents;
    Long excusedStudents;
    Double attendanceRate; // Tỉ lệ phần trăm sinh viên có mặt (present + late)
}