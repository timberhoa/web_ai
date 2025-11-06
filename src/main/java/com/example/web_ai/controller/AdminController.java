package com.example.web_ai.controller;

import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.AttendanceStatsResponse;
import com.example.web_ai.dto.response.ClassSessionResponse;
import com.example.web_ai.dto.response.FacultyStudentStatsResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.enums.Role;
import com.example.web_ai.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getUser());
    }

    @GetMapping("/users/filter")
    public ResponseEntity<List<UserResponse>> filterUsers(@RequestParam("role") Role role) {
        return ResponseEntity.ok(adminService.filter(role));
    }

    @PostMapping("/addUser")
    public ResponseEntity<UserResponse> addUser(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(adminService.addUser(userRequest));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id,
                                                   @RequestBody UserRequest req) {
        return ResponseEntity.ok(adminService.updateUser(id, req));
    }

    @GetMapping("/stats/faculty-students")
    public ResponseEntity<List<FacultyStudentStatsResponse>> getFacultyStudentStats() {
        return ResponseEntity.ok(adminService.getFacultyStudentStats());
    }

    @GetMapping("/stats/attendance/{sessionId}")
    public ResponseEntity<AttendanceStatsResponse> getAttendanceStatsBySession(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(adminService.getAttendanceStatsBySession(sessionId));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ClassSessionResponse>> getAllClassSessions() {
        return ResponseEntity.ok(adminService.getAllClassSessions());
    }

    @GetMapping("/debug/session/{sessionId}")
    public ResponseEntity<String> debugSession(@PathVariable UUID sessionId) {
        try {
            // Debug thông tin session
            return ResponseEntity.ok("Debug info for session: " + sessionId);
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

    @GetMapping("/debug/first-session-id")
    public ResponseEntity<String> getFirstSessionId() {
        return ResponseEntity.ok(adminService.getFirstSessionId());
    }

    @GetMapping("/debug/sessions-with-attendance")
    public ResponseEntity<List<String>> getSessionsWithAttendance() {
        return ResponseEntity.ok(adminService.getSessionsWithAttendance());
    }

}

// Tạo controller riêng cho debug không cần auth
@RestController
@RequestMapping("/api/debug")
class DebugController {
    
    private final AdminService adminService;
    
    public DebugController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<String>> getAllSessions() {
        return ResponseEntity.ok(adminService.getSessionsWithAttendance());
    }

    @GetMapping("/first-session-id")
    public ResponseEntity<String> getFirstSessionId() {
        return ResponseEntity.ok(adminService.getFirstSessionId());
    }

    @GetMapping("/test-attendance/{sessionId}")
    public ResponseEntity<AttendanceStatsResponse> testAttendance(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(adminService.getAttendanceStatsBySession(sessionId));
    }
}
