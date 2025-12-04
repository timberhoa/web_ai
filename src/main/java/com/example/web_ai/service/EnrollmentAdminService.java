package com.example.web_ai.service;

import com.example.web_ai.dto.request.EnrollStudentsRequest;
import com.example.web_ai.dto.response.EnrollmentBulkResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.entity.Course;
import com.example.web_ai.entity.Enrollment;
import com.example.web_ai.entity.User;
import com.example.web_ai.enums.Role;
import com.example.web_ai.exception.BadRequestException;
import com.example.web_ai.exception.NotFoundException;
import com.example.web_ai.mapper.UserMapper;
import com.example.web_ai.repository.CourseRepository;
import com.example.web_ai.repository.EnrollmentRepository;
import com.example.web_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EnrollmentAdminService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final UserMapper userMapper;

    public EnrollmentBulkResponse enrollStudents(EnrollStudentsRequest request) {
        if (request.getCourseId() == null) {
            throw new BadRequestException("COURSE_ID_REQUIRED");
        }
        if (request.getStudentIds() == null || request.getStudentIds().isEmpty()) {
            throw new BadRequestException("STUDENT_IDS_REQUIRED");
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("COURSE_NOT_FOUND"));

        List<UUID> added = new ArrayList<>();
        List<UUID> skipped = new ArrayList<>();

        for (UUID studentId : request.getStudentIds()) {
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND:" + studentId));
            if (student.getRole() != Role.STUDENT) {
                skipped.add(studentId);
                continue;
            }
            if (enrollmentRepository.existsByCourse_IdAndStudent_Id(course.getId(), student.getId())) {
                skipped.add(studentId);
                continue;
            }

            Enrollment e = new Enrollment();
            e.setCourse(course);
            e.setStudent(student);
            enrollmentRepository.save(e);
            added.add(studentId);
        }

        return EnrollmentBulkResponse.builder()
                .courseId(course.getId())
                .addedCount(added.size())
                .skippedCount(skipped.size())
                .addedStudentIds(added)
                .skippedStudentIds(skipped)
                .build();
    }

    public List<com.example.web_ai.dto.response.EnrollmentItemResponse> listStudentsByCourse(UUID courseId) {
        if (courseId == null)
            throw new BadRequestException("COURSE_ID_REQUIRED");
        if (!courseRepository.existsById(courseId))
            throw new NotFoundException("COURSE_NOT_FOUND");
        return enrollmentRepository.findByCourse_IdWithStudent(courseId).stream()
                .map(e -> com.example.web_ai.dto.response.EnrollmentItemResponse.builder()
                        .enrollmentId(e.getId())
                        .studentId(e.getStudent().getId())
                        .studentName(e.getStudent().getFullName())
                        .studentEmail(e.getStudent().getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteEnrollment(UUID enrollmentId) {
        Enrollment e = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("ENROLLMENT_NOT_FOUND"));
        enrollmentRepository.delete(e);
    }

    public EnrollmentBulkResponse seedEnrollments(com.example.web_ai.dto.request.SeedEnrollmentsRequest request) {
        if (request.getCourseId() == null) {
            throw new BadRequestException("COURSE_ID_REQUIRED");
        }
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("COURSE_NOT_FOUND"));

        List<User> candidates = userRepository.findByRole(Role.STUDENT);
        // filter by faculty if provided
        if (request.getFacultyId() != null) {
            candidates = candidates.stream()
                    .filter(u -> u.getFaculty() != null && request.getFacultyId().equals(u.getFaculty().getId()))
                    .collect(Collectors.toList());
        }

        Integer limit = request.getLimit();
        if (limit != null && limit > 0 && candidates.size() > limit) {
            candidates = candidates.subList(0, limit);
        }

        List<UUID> added = new ArrayList<>();
        List<UUID> skipped = new ArrayList<>();
        for (User student : candidates) {
            if (enrollmentRepository.existsByCourse_IdAndStudent_Id(course.getId(), student.getId())) {
                skipped.add(student.getId());
                continue;
            }
            Enrollment e = new Enrollment();
            e.setCourse(course);
            e.setStudent(student);
            enrollmentRepository.save(e);
            added.add(student.getId());
        }

        return EnrollmentBulkResponse.builder()
                .courseId(course.getId())
                .addedCount(added.size())
                .skippedCount(skipped.size())
                .addedStudentIds(added)
                .skippedStudentIds(skipped)
                .build();
    }
}
