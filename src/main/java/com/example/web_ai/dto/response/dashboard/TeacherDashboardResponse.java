package com.example.web_ai.dto.response.dashboard;

import com.example.web_ai.dto.response.ClassSessionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class TeacherDashboardResponse {
    long totalCourses;
    long sessionsToday;
    long totalCheckinsToday;
    long absentToday;
    long lateToday;
    List<ClassSessionResponse> upcomingSessions;
}

