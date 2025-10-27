package com.example.web_ai.service;

import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.entity.Enrollment;
import com.example.web_ai.mapper.CourseMapper;
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
    private final CourseMapper courseMapper;

    public List<CourseResponse> getAllCoursesByStudentId(UUID studentId){
        List<Enrollment> enrollments = enrollmentRepository.findByStudent_Id(studentId);

        log.info("🔹 Enrollments: {}", enrollments.size());

        return enrollments.stream()
                .map(e -> {
                    log.info("🔹 Enrollment ID: {}, Course Name: {}, Course Code: {}",
                            e.getId(),
                            e.getCourse().getName(),
                            e.getCourse().getCode());

//                    CourseResponse courseResponse = courseMapper.toResponse(e.getCourse());
//                    courseResponse.setTeacher_id(e.getCourse().getTeacher().getId());
                    CourseResponse courseResponse = CourseResponse.fromEntity(e.getCourse());
                    return courseResponse;
                })
                .toList();
    }
}
