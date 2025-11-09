package com.example.web_ai.controller;

import com.example.web_ai.dto.request.EnrollStudentsRequest;
import com.example.web_ai.dto.response.EnrollmentBulkResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.service.EnrollmentAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/enrollments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Enrollment (Admin)", description = "Manage enrollments for courses")
public class EnrollmentAdminController {

    private final EnrollmentAdminService enrollmentAdminService;

    @PostMapping
    @Operation(summary = "Bulk enroll students", description = "Add multiple students to a course in one call.")
    public ResponseEntity<EnrollmentBulkResponse> enrollStudents(@RequestBody EnrollStudentsRequest request) {
        return ResponseEntity.ok(enrollmentAdminService.enrollStudents(request));
    }

    @GetMapping
    @Operation(summary = "List students by course", description = "Show all enrolled students for a course.")
    public ResponseEntity<List<com.example.web_ai.dto.response.EnrollmentItemResponse>> listStudentsByCourse(@RequestParam("courseId") UUID courseId) {
        return ResponseEntity.ok(enrollmentAdminService.listStudentsByCourse(courseId));
    }

    @DeleteMapping("/{enrollmentId}")
    @Operation(summary = "Delete enrollment", description = "Remove a student from a course.")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable UUID enrollmentId) {
        enrollmentAdminService.deleteEnrollment(enrollmentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/seed")
    @Operation(summary = "Seed enrollments", description = "Populate a course with students (by faculty/limit) for demo/testing.")
    public ResponseEntity<EnrollmentBulkResponse> seedEnrollments(@RequestBody com.example.web_ai.dto.request.SeedEnrollmentsRequest request) {
        return ResponseEntity.ok(enrollmentAdminService.seedEnrollments(request));
    }
}
