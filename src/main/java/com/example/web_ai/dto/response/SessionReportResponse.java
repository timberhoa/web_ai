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
public class SessionReportResponse {
    UUID reportId;
    String reportType;
    String title;
    LocalDateTime generatedAt;
    String generatedBy;
    
    // Filter criteria
    UUID courseId;
    String courseName;
    String courseCode;
    UUID teacherId;
    String teacherName;
    LocalDateTime fromDate;
    LocalDateTime toDate;
    
    // Summary statistics
    Long totalSessions;
    Long totalStudents;
    Long totalPresent;
    Long totalLate;
    Long totalAbsent;
    Long totalExcused;
    Double averageAttendanceRate;
    
    // Detailed sessions
    List<SessionReportDetailItem> sessions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = PRIVATE)
    public static class SessionReportDetailItem {
        UUID sessionId;
        LocalDateTime startTime;
        LocalDateTime endTime;
        String roomName;
        boolean locked;
        Long totalEnrolled;
        Long presentCount;
        Long lateCount;
        Long absentCount;
        Long excusedCount;
        Double attendanceRate;
    }
}

