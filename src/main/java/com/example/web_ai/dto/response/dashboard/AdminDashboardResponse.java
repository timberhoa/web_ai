package com.example.web_ai.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AdminDashboardResponse {
    long totalStudents;
    long totalCourses;
    long sessionsToday;
    long checkinsToday;
    double attendanceRate;
}

