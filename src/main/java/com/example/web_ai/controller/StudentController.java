package com.example.web_ai.controller;

import com.example.web_ai.dto.response.ClassSessionResponse;
import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.dto.response.StudentAttendanceHistoryResponse;
import com.example.web_ai.dto.response.StudentAttendanceStatsResponse;
import com.example.web_ai.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Student", description = "Student self-service APIs (schedule, attendance, stats)")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/courses")
    @Operation(summary = "List my courses", description = "Return every course the authenticated student enrolled in.")
    public ResponseEntity<List<CourseResponse>> getMyCourses(Authentication authentication) {
        UUID studentId = extractUserId(authentication);
        return ResponseEntity.ok(studentService.getMyCourses(studentId));
    }

    @GetMapping("/sessions")
    @Operation(summary = "Search my sessions",
            description = "Return paged sessions for the student's enrolled courses (filter by date range).")
    public ResponseEntity<Page<ClassSessionResponse>> getMySessions(Authentication authentication,
                                                                    @RequestParam(value = "from", required = false)
                                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                    LocalDateTime from,
                                                                    @RequestParam(value = "to", required = false)
                                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                    LocalDateTime to,
                                                                    @PageableDefault(size = 10, sort = "startTime")
                                                                    Pageable pageable) {
        UUID studentId = extractUserId(authentication);
        return ResponseEntity.ok(studentService.getMySessions(studentId, from, to, pageable));
    }

    @GetMapping("/attendance/history")
    @Operation(summary = "Attendance history",
            description = "Return paged attendance records for the student, filterable by course/date.")
    public ResponseEntity<Page<StudentAttendanceHistoryResponse>> getMyAttendanceHistory(Authentication authentication,
                                                                                         @RequestParam(value = "courseId", required = false)
                                                                                         UUID courseId,
                                                                                         @RequestParam(value = "from", required = false)
                                                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                                         LocalDateTime from,
                                                                                         @RequestParam(value = "to", required = false)
                                                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                                         LocalDateTime to,
                                                                                         @PageableDefault(size = 20, sort = "checkedAt")
                                                                                         Pageable pageable) {
        UUID studentId = extractUserId(authentication);
        return ResponseEntity.ok(studentService.getMyAttendanceHistory(studentId, courseId, from, to, pageable));
    }

    @GetMapping("/attendance/stats")
    @Operation(summary = "Attendance stats",
            description = "Aggregated attendance statistics per course for the student.")
    public ResponseEntity<List<StudentAttendanceStatsResponse>> getMyAttendanceStats(Authentication authentication,
                                                                                     @RequestParam(value = "courseId", required = false)
                                                                                     UUID courseId,
                                                                                     @RequestParam(value = "from", required = false)
                                                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                                     LocalDateTime from,
                                                                                     @RequestParam(value = "to", required = false)
                                                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                                     LocalDateTime to) {
        UUID studentId = extractUserId(authentication);
        return ResponseEntity.ok(studentService.getMyAttendanceStats(studentId, courseId, from, to));
    }

    private UUID extractUserId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(jwt.getClaim("id"));
    }
}
