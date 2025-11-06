package com.example.web_ai.service;

import com.example.web_ai.dto.request.CourseRequest;
import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.entity.Course;
import com.example.web_ai.entity.Faculty;
import com.example.web_ai.entity.User;
import com.example.web_ai.repository.CourseRepository;
import com.example.web_ai.repository.FacultyRepository;
import com.example.web_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;

    public CourseResponse addCourse(CourseRequest req) {
        if (req.getCode() == null || req.getCode().isBlank())
            throw new IllegalArgumentException("CODE_REQUIRED");
        if (req.getName() == null || req.getName().isBlank())
            throw new IllegalArgumentException("NAME_REQUIRED");
        if (courseRepository.existsByCode(req.getCode()))
            throw new RuntimeException("COURSE_CODE_ALREADY_EXISTS");

        User teacher = null;
        if (req.getTeacher_id() != null) {
            teacher = userRepository.findById(req.getTeacher_id())
                    .orElseThrow(() -> new RuntimeException("TEACHER_NOT_FOUND"));
        }

        Faculty faculty = null;
        if (req.getFaculty_id() != null) {
            faculty = facultyRepository.findById(req.getFaculty_id())
                    .orElseThrow(() -> new RuntimeException("FACULTY_NOT_FOUND"));
        }

        Course course = new Course();
        course.setCode(req.getCode());
        course.setName(req.getName());
        course.setTeacher(teacher);
        course.setCredits(req.getCredits());
        course.setFaculty(faculty);

        Course saved = courseRepository.save(course);
        return CourseResponse.fromEntity(saved);
    }

    public CourseResponse updateCourse(UUID id, CourseRequest req) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("COURSE_NOT_FOUND"));

        if (req.getCode() != null && !req.getCode().isBlank()) {
            if (!req.getCode().equals(course.getCode()) && courseRepository.existsByCode(req.getCode())) {
                throw new RuntimeException("COURSE_CODE_ALREADY_EXISTS");
            }
            course.setCode(req.getCode());
        }

        if (req.getName() != null && !req.getName().isBlank()) {
            course.setName(req.getName());
        }

        if (req.getTeacher_id() != null) {
            User teacher = userRepository.findById(req.getTeacher_id())
                    .orElseThrow(() -> new RuntimeException("TEACHER_NOT_FOUND"));
            course.setTeacher(teacher);
        }

        if (req.getCredits() != null) {
            course.setCredits(req.getCredits());
        }

        if (req.getFaculty_id() != null) {
            Faculty faculty = facultyRepository.findById(req.getFaculty_id())
                    .orElseThrow(() -> new RuntimeException("FACULTY_NOT_FOUND"));
            course.setFaculty(faculty);
        }

        Course saved = courseRepository.save(course);
        return CourseResponse.fromEntity(saved);
    }

    public void deleteCourse(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("COURSE_NOT_FOUND"));
        courseRepository.delete(course);
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public CourseResponse getCourse(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("COURSE_NOT_FOUND"));
        return CourseResponse.fromEntity(course);
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public List<CourseResponse> listCourses() {
        return courseRepository.findAll().stream()
                .map(CourseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public List<CourseResponse> getListCourseByFaculty(String facultyCode) {
        if (facultyCode == null) throw new IllegalArgumentException("FACULTY_ID_REQUIRED");
        if (!facultyRepository.existsByCode(facultyCode)) {
            throw new RuntimeException("FACULTY_NOT_FOUND");
        }
        return courseRepository.findAllByFaculty_code(facultyCode).stream()
                .map(CourseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Method chỉ dành cho ADMIN để lấy tất cả course
    public List<CourseResponse> getAllCoursesForAdmin() {
        return courseRepository.findAll().stream()
                .map(CourseResponse::fromEntity)
                .collect(Collectors.toList());
    }

}
