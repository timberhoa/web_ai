package com.example.web_ai.service;

import com.example.web_ai.dto.request.ResetPassword;
import com.example.web_ai.dto.request.UpdateProfileMeRequest;
import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.entity.User;
import com.example.web_ai.exception.NotFoundException;
import com.example.web_ai.mapper.UserMapper;
import com.example.web_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponse getUserById(UUID id) {
        User u = userRepository.findUserById(id)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        return UserResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .username(u.getUsername())
                .email(u.getEmail())
                .phone(u.getPhone())
                .role(u.getRole())
                .build();
    }

    public UserResponse getUserByUsername(String username){
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        return userMapper.toResponse(user);
    }

    public UserResponse updateProfileMe(String username, UpdateProfileMeRequest req){
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User Not Found"));
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getUsername() != null) user.setUsername(req.getUsername());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getPhone() != null) user.setPhone(req.getPhone());

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public UserResponse updateUser(UUID id, UserRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (req.getFullName() != null) u.setFullName(req.getFullName());
        if (req.getUsername() != null) u.setUsername(req.getUsername());
        if (req.getEmail() != null) u.setEmail(req.getEmail());
        if (req.getPhone() != null) u.setPhone(req.getPhone());

        User saved = userRepository.save(u);

        return UserResponse.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .role(saved.getRole())
                .build();
    }

    public void updatePassword(UUID id, ResetPassword req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (req.getConfirmPassword() != null &&
                !req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("PASSWORD_CONFIRM_NOT_MATCH");
        }

        if (!passwordEncoder.matches(req.getOldPassword(), u.getPassword())) {
            throw new RuntimeException("OLD_PASSWORD_INCORRECT");
        }

        if (req.getNewPassword().length() < 8) {
            throw new RuntimeException("PASSWORD_TOO_WEAK");
        }
        if (passwordEncoder.matches(req.getNewPassword(), u.getPassword())) {
            throw new RuntimeException("NEW_PASSWORD_MUST_DIFFER_FROM_OLD");
        }

        u.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(u);
    }
}
