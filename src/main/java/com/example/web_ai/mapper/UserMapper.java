package com.example.web_ai.mapper;

import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.FacultySimpleResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.entity.Faculty;
import com.example.web_ai.entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        if (request == null) return null;
        User u = new User();
        u.setFullName(request.getFullName());
        u.setUsername(request.getUsername());
        u.setEmail(request.getEmail());
        u.setPhone(request.getPhone());
        u.setRole(request.getRole());
        if (request.getFacultyId() != null) {
            Faculty f = new Faculty();
            f.setId(request.getFacultyId());
            u.setFaculty(f);
        }
        // password, active and other relations are set by services
        return u;
    }

    public UserResponse toResponse(User user) {
        if (user == null) return null;
        FacultySimpleResponse faculty = null;
        if (user.getFaculty() != null) {
            faculty = FacultySimpleResponse.builder()
                    .id(user.getFaculty().getId())
                    .code(user.getFaculty().getCode())
                    .name(user.getFaculty().getName())
                    .build();
        }
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .active(user.isActive())
                .faculty(faculty)
                .build();
    }
}
