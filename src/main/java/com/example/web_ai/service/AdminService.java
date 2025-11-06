package com.example.web_ai.service;

import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.AttendanceStatsResponse;
import com.example.web_ai.dto.response.FacultyStudentStatsResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.entity.User;
import com.example.web_ai.enums.Role;
import com.example.web_ai.repository.AttendanceRespository;
import com.example.web_ai.repository.AuthRepository;
import com.example.web_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminService {
    private final UserRepository userRepository;
    private final AttendanceRespository attendanceRespository;
    private final PasswordEncoder passwordEncoder;


    public List<UserResponse> getUser() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .phone(u.getPhone())
                        .role(u.getRole())
                        .build())
                .collect(Collectors.toList());
    }

    public List<UserResponse> filter(Role req) {
        List<User> users = userRepository.findByRole(req);
        return users.stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .phone(u.getPhone())
                        .role(u.getRole())
                        .build())
                .collect(Collectors.toList());
    }

    public UserResponse addUser(UserRequest req) {
        if (req.getUsername() == null || req.getUsername().isBlank()) {
            throw new IllegalArgumentException("USERNAME_REQUIRED");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new IllegalArgumentException("PASSWORD_REQUIRED");
        }
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new IllegalArgumentException("EMAIL_REQUIRED");
        }

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("USERNAME_ALREADY_EXISTS");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("EMAIL_ALREADY_EXISTS");
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setRole(req.getRole());
        user.setActive(true);

        userRepository.save(user);

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .role(saved.getRole())
                .build();
    }

    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        user.setActive(false);
        userRepository.save(user);
    }

    public UserResponse updateUser(UUID id, UserRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (req.getFullName() != null && !req.getFullName().isBlank()) {
            user.setFullName(req.getFullName());
        }
        if (req.getUsername() != null && !req.getUsername().isBlank()) {
            if (userRepository.existsByUsername(req.getUsername()) &&
                    !req.getUsername().equals(user.getUsername())) {
                throw new RuntimeException("USERNAME_ALREADY_EXISTS");
            }
            user.setUsername(req.getUsername());
        }
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            if (userRepository.existsByEmail(req.getEmail()) &&
                    !req.getEmail().equals(user.getEmail())) {
                throw new RuntimeException("EMAIL_ALREADY_EXISTS");
            }
            user.setEmail(req.getEmail());
        }
        if (req.getPhone() != null) {
            user.setPhone(req.getPhone());
        }
        if (req.getRole() != null) {
            user.setRole(req.getRole());
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getActive() != null) {
            user.setActive(req.getActive());
        }

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .role(saved.getRole())
                .active(saved.isActive())
                .build();
    }

    public List<FacultyStudentStatsResponse> getFacultyStudentStats() {
        List<Object[]> results = userRepository.findStudentCountByFaculty();
        
        return results.stream()
                .map(result -> FacultyStudentStatsResponse.builder()
                        .facultyId((UUID) result[0])
                        .facultyCode((String) result[1])
                        .facultyName((String) result[2])
                        .studentCount((Long) result[3])
                        .build())
                .collect(Collectors.toList());
    }

    public AttendanceStatsResponse getAttendanceStatsBySession(UUID sessionId) {
        Object[] result = attendanceRespository.findAttendanceStatsBySessionId(sessionId);
        
        if (result == null) {
            throw new RuntimeException("SESSION_NOT_FOUND");
        }

        UUID sessionIdResult = (UUID) result[0];
        String courseName = (String) result[1];
        String courseCode = (String) result[2];
        String roomName = (String) result[3];
        Long totalEnrolled = (Long) result[4];
        Long presentCount = result[5] != null ? ((Number) result[5]).longValue() : 0L;
        Long lateCount = result[6] != null ? ((Number) result[6]).longValue() : 0L;
        Long absentCount = result[7] != null ? ((Number) result[7]).longValue() : 0L;
        Long excusedCount = result[8] != null ? ((Number) result[8]).longValue() : 0L;

        // Tính tỉ lệ có mặt (present + late) / total enrolled
        Double attendanceRate = totalEnrolled > 0 ? 
            ((double) (presentCount + lateCount) / totalEnrolled) * 100 : 0.0;

        return AttendanceStatsResponse.builder()
                .sessionId(sessionIdResult)
                .courseName(courseName)
                .courseCode(courseCode)
                .roomName(roomName)
                .totalEnrolledStudents(totalEnrolled)
                .presentStudents(presentCount)
                .lateStudents(lateCount)
                .absentStudents(absentCount)
                .excusedStudents(excusedCount)
                .attendanceRate(Math.round(attendanceRate * 100.0) / 100.0) // Làm tròn 2 chữ số
                .build();
    }




}
