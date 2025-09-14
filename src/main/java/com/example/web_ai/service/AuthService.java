package com.example.web_ai.service;

import com.example.web_ai.dto.request.LoginRequest;
import com.example.web_ai.dto.request.Register;
import com.example.web_ai.dto.response.AuthResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.entity.User;
import com.example.web_ai.repository.AuthRepository;
import com.example.web_ai.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(Register req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Username already exists");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already exists");
        if (userRepository.existsByPhone(req.getPhone()))
            throw new IllegalArgumentException("Phone already exists");

        User user = new User();
        user.setFullName(req.getFullName());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setRole(req.getRole());
        user.setActive(true);

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("WRONG_PASSWORD");
        }

        String token = JwtUtil.generateToken(user);
        return new AuthResponse(token, "Bearer");
    }

    public String logout() {
        return "Logged out successfully. Please remove token on client side.";
    }
}
