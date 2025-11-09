package com.example.web_ai.controller;

import com.example.web_ai.dto.request.LoginRequest;
import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.AuthResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.service.AuthService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication and registration")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Create a new user account")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate and receive JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate the current session (stateless info-only)")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok(authService.logout());
    }
}
