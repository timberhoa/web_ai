package com.example.web_ai.service;

import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.AttendanceStatsResponse;
import com.example.web_ai.dto.response.ClassSessionResponse;
import com.example.web_ai.dto.response.FacultyStudentStatsResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.entity.ClassSession;
import com.example.web_ai.entity.User;
import com.example.web_ai.enums.Role;
import com.example.web_ai.exception.BadRequestException;
import com.example.web_ai.exception.NotFoundException;
import com.example.web_ai.mapper.UserMapper;
import com.example.web_ai.repository.AttendanceRespository;
import com.example.web_ai.repository.ClassSessionRepository;
import com.example.web_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ClassSessionRepository classSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    public Page<UserResponse> getUser(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toResponse);
    }

    public Page<UserResponse> filter(Role role, Pageable pageable) {
        Page<User> users = userRepository.findByRole(role, pageable);
        return users.map(userMapper::toResponse);
    }

    public UserResponse addUser(UserRequest req) {
        if (req.getUsername() == null || req.getUsername().isBlank()) {
            throw new BadRequestException("USERNAME_REQUIRED");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new BadRequestException("PASSWORD_REQUIRED");
        }
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new BadRequestException("EMAIL_REQUIRED");
        }

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BadRequestException("USERNAME_ALREADY_EXISTS");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("EMAIL_ALREADY_EXISTS");
        }

        User user = new User();
        user.setFullName(req.getFullName());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setRole(req.getRole());
        user.setActive(true);

        User saved = userRepository.save(user);

        return userMapper.toResponse(saved);
    }

    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        user.setActive(false);
        userRepository.save(user);
    }

    public UserResponse updateUser(UUID id, UserRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        if (req.getFullName() != null && !req.getFullName().isBlank()) {
            user.setFullName(req.getFullName());
        }
        if (req.getUsername() != null && !req.getUsername().isBlank()) {
            if (userRepository.existsByUsername(req.getUsername()) &&
                    !req.getUsername().equals(user.getUsername())) {
                throw new BadRequestException("USERNAME_ALREADY_EXISTS");
            }
            user.setUsername(req.getUsername());
        }
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            if (userRepository.existsByEmail(req.getEmail()) &&
                    !req.getEmail().equals(user.getEmail())) {
                throw new BadRequestException("EMAIL_ALREADY_EXISTS");
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

        return userMapper.toResponse(saved);
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
        ClassSession session = classSessionRepository.findClassSessionById(sessionId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));
        // Print log here to check if session exist or not

        Object[] result = attendanceRespository.findAttendanceStatsBySessionId(sessionId);
        
        if (result == null || result.length == 0) {
            // Nếu không có dữ liệu attendance, tạo response với dữ liệu từ session
            return AttendanceStatsResponse.builder()
                    .sessionId(sessionId)
                    .courseName(session.getCourse().getName())
                    .courseCode(session.getCourse().getCode())
                    .roomName(session.getRoomName())
                    .totalEnrolledStudents(0L)
                    .presentStudents(0L)
                    .lateStudents(0L)
                    .absentStudents(0L)
                    .excusedStudents(0L)
                    .attendanceRate(0.0)
                    .build();
        }

        try {
            // Parse kết quả từ query
            String courseName = result[0] != null ? result[0].toString() : session.getCourse().getName();
            String courseCode = result[1] != null ? result[1].toString() : session.getCourse().getCode();
            String roomName = result[2] != null ? result[2].toString() : session.getRoomName();
            Long totalEnrolled = result[3] != null ? ((Number) result[3]).longValue() : 0L;
            Long presentCount = result[4] != null ? ((Number) result[4]).longValue() : 0L;
            Long lateCount = result[5] != null ? ((Number) result[5]).longValue() : 0L;
            Long absentCount = result[6] != null ? ((Number) result[6]).longValue() : 0L;
            Long excusedCount = result[7] != null ? ((Number) result[7]).longValue() : 0L;

            // Tính tỉ lệ có mặt (present + late) / total enrolled
            Double attendanceRate = totalEnrolled > 0 ? 
                ((double) (presentCount + lateCount) / totalEnrolled) * 100 : 0.0;

            return AttendanceStatsResponse.builder()
                    .sessionId(sessionId)
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
        } catch (Exception e) {
            throw new BadRequestException("Error processing attendance stats: " + e.getMessage() + " (Array length: " + result.length + ")");
        }
    }

    public Page<ClassSessionResponse> getAllClassSessions(Pageable pageable) {
        Page<ClassSession> sessions = classSessionRepository.findAll(pageable);
        
        return sessions.map(session -> ClassSessionResponse.builder()
                .sessionId(session.getId())
                .courseName(session.getCourse().getName())
                .courseCode(session.getCourse().getCode())
                .roomName(session.getRoomName())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .build());
    }

    public String getFirstSessionId() {
        List<ClassSession> sessions = classSessionRepository.findAll();
        if (!sessions.isEmpty()) {
            return sessions.get(0).getId().toString();
        }
        return "No sessions found";
    }

    public List<String> getSessionsWithAttendance() {
        List<ClassSession> sessions = classSessionRepository.findAll();
        return sessions.stream()
                .map(session -> session.getId().toString() + " - " + 
                      session.getCourse().getName() + " (" + session.getRoomName() + ")")
                .collect(Collectors.toList());
    }




}
