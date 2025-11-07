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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
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
}
