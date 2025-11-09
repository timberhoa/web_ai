package com.example.web_ai.dto.response.dashboard;

import com.example.web_ai.dto.response.ClassSessionResponse;
import com.example.web_ai.dto.response.StudentAttendanceHistoryResponse;
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
public class StudentDashboardResponse {
    long totalCourses;
    long todayCheckins;
    boolean faceRegistered;
    List<ClassSessionResponse> todaySessions;
    StudentAttendanceHistoryResponse latestAttendance;
}

