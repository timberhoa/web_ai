package com.example.web_ai.dto.response;

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
public class SessionAttendanceSummaryResponse {
    UUID sessionId;
    UUID courseId;
    String courseName;
    String courseCode;
    UUID teacherId;
    String teacherName;
    String roomName;
    LocalDateTime startTime;
    LocalDateTime endTime;
    boolean locked;
    Long totalEnrolled;
    Long totalMarked;
    Long presentCount;
    Long lateCount;
    Long absentCount;
    Long excusedCount;
}
