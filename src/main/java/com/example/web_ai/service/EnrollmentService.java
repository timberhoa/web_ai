package com.example.web_ai.service;

import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.entity.Enrollment;
import com.example.web_ai.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public List<CourseResponse> getAllCoursesByStudentId(UUID studentId){
        List<Enrollment> enrollments = enrollmentRepository.findByStudent_IdWithCourseDetails(studentId);

        log.info("🔹 Enrollments: {}", enrollments.size());

        return enrollments.stream()
                .map(enrollment -> {
                    log.info("🔹 Enrollment ID: {}, Course Name: {}, Course Code: {}",
                            enrollment.getId(),
                            enrollment.getCourse().getName(),
                            enrollment.getCourse().getCode());

                    return CourseResponse.fromEntity(enrollment.getCourse());
                })
                .toList();
    }
}
