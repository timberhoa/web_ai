package com.example.web_ai.controller;

import com.example.web_ai.dto.response.dashboard.AdminDashboardResponse;
import com.example.web_ai.dto.response.dashboard.StudentDashboardResponse;
import com.example.web_ai.dto.response.dashboard.TeacherDashboardResponse;
import com.example.web_ai.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Widgets for admin, teacher, and student overview screens")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin dashboard overview",
            description = "Return system-wide KPIs such as total students/courses, today's sessions and attendance rate.")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Teacher dashboard overview",
            description = "Return KPIs for the authenticated teacher (classes, sessions today, attendance summary, upcoming sessions).")
    public ResponseEntity<TeacherDashboardResponse> getTeacherDashboard(Authentication authentication) {
        UUID teacherId = extractUserId(authentication);
        return ResponseEntity.ok(dashboardService.getTeacherDashboard(teacherId));
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Student dashboard overview",
            description = "Return today's timetable, latest attendance, and status flags for the authenticated student.")
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard(Authentication authentication) {
        UUID studentId = extractUserId(authentication);
        return ResponseEntity.ok(dashboardService.getStudentDashboard(studentId));
    }

    private UUID extractUserId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(jwt.getClaim("id"));
    }
}

