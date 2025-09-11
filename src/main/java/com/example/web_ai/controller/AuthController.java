package com.example.web_ai.controller;

import com.example.web_ai.dto.request.Register;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody Register request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
}
