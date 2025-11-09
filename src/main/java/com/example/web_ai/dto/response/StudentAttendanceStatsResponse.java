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
public class StudentAttendanceStatsResponse {
    UUID courseId;
    String courseName;
    String courseCode;
    long totalSessions;
    long attendedSessions;
    long presentCount;
    long lateCount;
    long excusedCount;
    long absentCount;
    double attendanceRate;
}

