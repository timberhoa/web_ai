package com.example.web_ai.service;

import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.entity.Enrollment;
import com.example.web_ai.mapper.CourseMapper;
import com.example.web_ai.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper;

    public List<CourseResponse> getAllCoursesByStudentId(UUID studentId){
        List<Enrollment> enrollments = enrollmentRepository.findByStudent_Id(studentId);

        return enrollments.stream()
                .map(e -> courseMapper.toResponse(e.getCourse()))
                .toList();
    }
}
