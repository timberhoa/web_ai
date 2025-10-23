package com.example.web_ai.service;

import com.example.web_ai.dto.request.CheckAttendanceRequest;
import com.example.web_ai.dto.response.CheckAttendanceResponse;
import com.example.web_ai.entity.Attendance;
import com.example.web_ai.entity.ClassSession;
import com.example.web_ai.entity.User;
import com.example.web_ai.exception.NotFoundException;
import com.example.web_ai.mapper.AttendanceMapper;
import com.example.web_ai.repository.AttendanceRespository;
import com.example.web_ai.repository.ClassSessionRepository;
import com.example.web_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRespository attendanceRespository;
    private final UserRepository userRepository;
    private final ClassSessionRepository classSessionRepository;
    private final AttendanceMapper attendanceMapper;

    @PreAuthorize("hasRole('TEACHER')")
    public CheckAttendanceResponse checkAttendance(CheckAttendanceRequest req){

        Attendance attendance = attendanceMapper.toEntity(req);

        User student = userRepository.findUserById(req.getStudent_id())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        ClassSession session = classSessionRepository.findClassSessionById(req.getSession_id())
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));

        attendance.setStudent(student);
        attendance.setSession(session);

        attendanceRespository.save(attendance);

        CheckAttendanceResponse res = attendanceMapper.toResponse(attendance);
        res.setMessage("Check attendance successfully");
        res.setStudentName(student.getFullName());
        return res;
    }
}
