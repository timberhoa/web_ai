package com.example.web_ai.service;

import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.entity.Enrollment;
import com.example.web_ai.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public Page<CourseResponse> getAllCoursesByStudentId(UUID studentId, Pageable pageable){
        Page<Enrollment> enrollments = enrollmentRepository.findByStudent_IdWithCourseDetails(studentId, pageable);

        log.info("🔹 Enrollments: {}", enrollments.getTotalElements());

        return enrollments.map(enrollment -> {
            log.info("🔹 Enrollment ID: {}, Course Name: {}, Course Code: {}",
                    enrollment.getId(),
                    enrollment.getCourse().getName(),
                    enrollment.getCourse().getCode());

            return CourseResponse.fromEntity(enrollment.getCourse());
        });
    }
}
