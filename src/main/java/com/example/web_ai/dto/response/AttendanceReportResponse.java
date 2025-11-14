package com.example.web_ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AttendanceReportResponse {
    UUID reportId;
    String reportType;
    String title;
    LocalDateTime generatedAt;
    String generatedBy;
    
    // Filter criteria
    UUID courseId;
    String courseName;
    String courseCode;
    UUID sessionId;
    UUID studentId;
    String studentName;
    LocalDateTime fromDate;
    LocalDateTime toDate;
    
    // Summary statistics
    Long totalSessions;
    Long totalStudents;
    Long presentCount;
    Long lateCount;
    Long absentCount;
    Long excusedCount;
    Double attendanceRate;
    
    // Detailed records
    List<AttendanceReportDetailItem> details;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = PRIVATE)
    public static class AttendanceReportDetailItem {
        UUID sessionId;
        LocalDateTime sessionStartTime;
        LocalDateTime sessionEndTime;
        String roomName;
        UUID studentId;
        String studentName;
        String studentEmail;
        String status;
        LocalDateTime checkedAt;
        String note;
    }
}

