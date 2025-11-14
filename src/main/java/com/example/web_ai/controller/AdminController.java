package com.example.web_ai.controller;

import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.AttendanceStatsResponse;
import com.example.web_ai.dto.response.ClassSessionResponse;
import com.example.web_ai.dto.response.FacultyStudentStatsResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.enums.Role;
import com.example.web_ai.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN','TEACHER')")
@Tag(name = "Admin", description = "Admin management APIs: users, sessions, statistics")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "List users", description = "Paged list of all users. Supports sorting and pagination.")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 10, sort = "fullName") Pageable pageable) {
        return ResponseEntity.ok(adminService.getUser(pageable));
    }

    @GetMapping("/users/filter")
    @Operation(summary = "Filter users by role", description = "Return users filtered by role (ADMIN/TEACHER/STUDENT) with pagination.")
    public ResponseEntity<Page<UserResponse>> filterUsers(
            @RequestParam("role") Role role,
            @PageableDefault(size = 10, sort = "fullName") Pageable pageable) {
        return ResponseEntity.ok(adminService.filter(role, pageable));
    }

    @PostMapping("/addUser")
    @Operation(summary = "Create a user", description = "Add a new user with role and faculty assignment.")
    public ResponseEntity<UserResponse> addUser(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(adminService.addUser(userRequest));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a user", description = "Remove a user by ID.")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update a user", description = "Edit user profile details and role.")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id,
            @RequestBody UserRequest req) {
        return ResponseEntity.ok(adminService.updateUser(id, req));
    }

    @GetMapping("/stats/faculty-students")
    @Operation(summary = "Faculty vs student counts", description = "Aggregate number of students per faculty.")
    public ResponseEntity<List<FacultyStudentStatsResponse>> getFacultyStudentStats() {
        return ResponseEntity.ok(adminService.getFacultyStudentStats());
    }

    @GetMapping("/stats/attendance/{sessionId}")
    @Operation(summary = "Attendance stats by session", description = "Return counts/percentages for a specific class session.")
    public ResponseEntity<AttendanceStatsResponse> getAttendanceStatsBySession(@PathVariable UUID sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("SESSION_ID_REQUIRED");
        }
        return ResponseEntity.ok(adminService.getAttendanceStatsBySession(sessionId));
    }

    @GetMapping("/sessions")
    @Operation(summary = "List all sessions", description = "Admin view of every class session (paged).")
    public ResponseEntity<Page<ClassSessionResponse>> getAllClassSessions(
            @PageableDefault(size = 10, sort = "startTime") Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllClassSessions(pageable));
    }
}
