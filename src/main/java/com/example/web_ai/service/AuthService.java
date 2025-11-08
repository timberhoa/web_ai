package com.example.web_ai.service;

import com.example.web_ai.exception.BadRequestException;
import com.example.web_ai.dto.request.LoginRequest;
import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.AuthResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.entity.User;
import com.example.web_ai.exception.NotFoundException;
import com.example.web_ai.exception.UnauthorizedException;
import com.example.web_ai.mapper.UserMapper;
import com.example.web_ai.repository.AuthRepository;
import com.example.web_ai.repository.FacultyRepository;
import com.example.web_ai.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse register(UserRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new BadRequestException("Username already exists");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new BadRequestException("Email already exists");
        if (userRepository.existsByPhone(req.getPhone()))
            throw new BadRequestException("Phone already exists");

        // Validate faculty if provided
        if (req.getFacultyId() != null) {
            if (!facultyRepository.existsById(req.getFacultyId())) {
                throw new BadRequestException("Faculty not found");
            }
        }

        User user = userMapper.toEntity(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setActive(true);

        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("WRONG_PASSWORD");
        }

        String token = JwtUtil.generateToken(user);
        return new AuthResponse(token, "Bearer");
    }

    public String logout() {
        return "Logged out successfully. Please remove token on client side.";
    }
}
