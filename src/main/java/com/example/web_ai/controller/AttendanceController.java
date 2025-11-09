package com.example.web_ai.controller;

import com.example.web_ai.dto.request.AttendanceUpdateRequest;
import com.example.web_ai.dto.request.CheckAttendanceRequest;
import com.example.web_ai.dto.request.SelfCheckAttendanceRequest;
import com.example.web_ai.dto.response.*;
import com.example.web_ai.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Capture check-ins, monitor live sessions and review attendance history")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @Operation(summary = "Check in/out a student", description = "Teachers confirm attendance for a student in a session.")
    public ResponseEntity<CheckAttendanceResponse> checkAttendance(@Valid @RequestBody CheckAttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.checkAttendance(request));
    }

    @PostMapping("/self")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Student self check-in",
            description = "Student marks own attendance for a session after face scan on client; validates time window and geo-fence if provided.")
    public ResponseEntity<CheckAttendanceResponse> selfCheck(Authentication authentication,
                                                             @Valid @RequestBody SelfCheckAttendanceRequest request) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return ResponseEntity.ok(attendanceService.selfCheck(UUID.fromString(jwt.getClaim("id")), request));
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Session attendance detail",
            description = "Return all attendance records and stats for a specific class session.")
    public ResponseEntity<SessionAttendanceDetailResponse> getSessionAttendance(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(attendanceService.getSessionAttendance(sessionId));
    }

    @GetMapping("/session/{sessionId}/roster")
    @Operation(summary = "Session roster",
            description = "List all enrolled students for the session's course with current attendance status.")
    public ResponseEntity<List<SessionRosterItemResponse>> getSessionRoster(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(attendanceService.getSessionRoster(sessionId));
    }

    @PutMapping("/{attendanceId}")
    @Operation(summary = "Update attendance record",
            description = "Fix mistakes or add notes for an existing attendance record before the session is locked.")
    public ResponseEntity<AttendanceRecordResponse> updateAttendance(@PathVariable UUID attendanceId,
                                                                     @Valid @RequestBody AttendanceUpdateRequest request) {
        return ResponseEntity.ok(attendanceService.updateAttendance(attendanceId, request));
    }

    @GetMapping("/monitor")
    @Operation(summary = "Monitor live sessions",
            description = "Monitor ongoing sessions within a configurable time window for real-time dashboards.")
    public ResponseEntity<List<SessionAttendanceSummaryResponse>> monitorSessions(
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "teacherId", required = false) UUID teacherId,
            @RequestParam(value = "minutesBefore", required = false) Integer minutesBefore,
            @RequestParam(value = "minutesAfter", required = false) Integer minutesAfter) {
        return ResponseEntity.ok(attendanceService.monitorSessions(courseId, teacherId, minutesBefore, minutesAfter));
    }

    @GetMapping("/review")
    @Operation(summary = "Review past sessions",
            description = "List sessions in a date range with aggregated attendance counts for closing and auditing.")
    public ResponseEntity<List<SessionAttendanceSummaryResponse>> reviewSessions(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "teacherId", required = false) UUID teacherId) {
        return ResponseEntity.ok(attendanceService.reviewSessions(from, to, courseId, teacherId));
    }

    @PostMapping("/review/{sessionId}/lock")
    @Operation(summary = "Lock a session", description = "Lock attendance so no further edits can be made.")
    public ResponseEntity<ClassSessionResponse> lockSession(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(attendanceService.toggleLock(sessionId, true));
    }

    @PostMapping("/review/{sessionId}/unlock")
    @Operation(summary = "Unlock a session", description = "Allow editing attendance again for a session.")
    public ResponseEntity<ClassSessionResponse> unlockSession(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(attendanceService.toggleLock(sessionId, false));
    }

    @PostMapping("/session/{sessionId}/seed")
    @Operation(summary = "Seed attendance records",
            description = "Create missing attendance rows for all enrolled students of the session's course with default ABSENT status.")
    public ResponseEntity<SeedAttendanceResponse> seedAttendance(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(attendanceService.seedAttendance(sessionId));
    }
}
