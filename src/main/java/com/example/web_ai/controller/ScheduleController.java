package com.example.web_ai.controller;

import com.example.web_ai.dto.request.ClassSessionRequest;
import com.example.web_ai.dto.request.RecurringSessionRequest;
import com.example.web_ai.dto.response.ClassSessionResponse;
import com.example.web_ai.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@Tag(name = "Schedule", description = "Manage class sessions and recurring schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Create a class session",
            description = "Defines a single class occurrence with time, room and optional geo-fence.")
    public ResponseEntity<ClassSessionResponse> createSession(@Valid @RequestBody ClassSessionRequest request) {
        return ResponseEntity.ok(scheduleService.createSession(request));
    }

    @PostMapping("/recurring")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Generate recurring sessions",
            description = "Generate multiple sessions from a weekly pattern between two dates.")
    public ResponseEntity<List<ClassSessionResponse>> generateRecurringSessions(
            @Valid @RequestBody RecurringSessionRequest request) {
        return ResponseEntity.ok(scheduleService.generateRecurringSessions(request));
    }

    @PutMapping("/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Update a session", description = "Edit time, room or geo-fence of a session.")
    public ResponseEntity<ClassSessionResponse> updateSession(@PathVariable UUID sessionId,
                                                              @Valid @RequestBody ClassSessionRequest request) {
        return ResponseEntity.ok(scheduleService.updateSession(sessionId, request));
    }

    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Delete a session", description = "Remove a class session if it is not locked.")
    public ResponseEntity<Void> deleteSession(@PathVariable UUID sessionId) {
        scheduleService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get session detail", description = "Fetch metadata about a specific class session.")
    public ResponseEntity<ClassSessionResponse> getSession(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(scheduleService.getSession(sessionId));
    }

    @GetMapping
    @Operation(summary = "Search sessions",
            description = "Filter sessions by course, lecturer or time range with pagination support.")
    public ResponseEntity<Page<ClassSessionResponse>> searchSessions(
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "teacherId", required = false) UUID teacherId,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20, sort = "startTime") Pageable pageable) {
        return ResponseEntity.ok(scheduleService.searchSessions(courseId, teacherId, from, to, pageable));
    }
}
