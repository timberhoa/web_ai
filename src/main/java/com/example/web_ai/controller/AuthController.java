package com.example.web_ai.controller;

import com.example.web_ai.dto.request.ForgotPasswordRequest;
import com.example.web_ai.dto.request.LoginRequest;
import com.example.web_ai.dto.request.ResetPasswordWithTokenRequest;
import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.AuthResponse;
import com.example.web_ai.dto.response.ForgotPasswordResponse;
import com.example.web_ai.dto.response.ResetPasswordResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.dto.response.ValidateTokenResponse;
import com.example.web_ai.service.AuthService;
import com.example.web_ai.service.UserService;
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
    private final UserService userService;

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

    // ========== Password Reset Endpoints ==========

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Send a password reset link to the user's email address")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse response = userService.createPasswordResetToken(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token", description = "Reset user password using a valid reset token")
    public ResponseEntity<ResetPasswordResponse> resetPassword(
            @Valid @RequestBody ResetPasswordWithTokenRequest request) {
        ResetPasswordResponse response = userService.resetPasswordWithToken(
                request.getToken(),
                request.getNewPassword(),
                request.getConfirmPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-reset-token")
    @Operation(summary = "Validate reset token", description = "Check if a password reset token is valid and not expired")
    public ResponseEntity<ValidateTokenResponse> validateResetToken(
            @RequestParam("token") String token) {
        ValidateTokenResponse response = userService.validatePasswordResetToken(token);
        return ResponseEntity.ok(response);
    }
}
