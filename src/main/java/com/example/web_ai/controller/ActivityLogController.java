package com.example.web_ai.controller;

import com.example.web_ai.dto.request.ActivityLogQueryRequest;
import com.example.web_ai.dto.response.ActivityLogResponse;
import com.example.web_ai.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/activity-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Activity Logs", description = "Audit logs for all web activities. Admin only.")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    @Operation(summary = "List activity logs", description = "Return paginated logs with optional filters and sorting.")
    public ResponseEntity<Page<ActivityLogResponse>> getLogs(ActivityLogQueryRequest filters,
            @PageableDefault(size = 20, sort = "occurredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(activityLogService.searchLogs(filters, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get log detail", description = "Return a single log entry by id.")
    public ResponseEntity<ActivityLogResponse> getLog(@PathVariable UUID id) {
        return ResponseEntity.ok(activityLogService.getLog(id));
    }
}
