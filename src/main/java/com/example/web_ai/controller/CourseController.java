package com.example.web_ai.controller;

import com.example.web_ai.dto.request.CourseRequest;
import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/")
    public ResponseEntity<CourseResponse> createCourse(@RequestBody @Valid CourseRequest req) {
        return ResponseEntity.ok(courseService.addCourse(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable UUID id,
                                                       @RequestBody @Valid CourseRequest req) {
        return ResponseEntity.ok(courseService.updateCourse(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable UUID id) {
        return ResponseEntity.ok(courseService.getCourse(id));
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> listCourses() {
        return ResponseEntity.ok(courseService.listCourses());
    }

    @GetMapping("/by-faculty/{faculty_code}")
    public ResponseEntity<List<CourseResponse>> getCourseByFacultyCode(@PathVariable String faculty_code) {
        return ResponseEntity.ok(courseService.getListCourseByFaculty(faculty_code));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<CourseResponse>> getAllCoursesForAdmin() {
        return ResponseEntity.ok(courseService.getAllCoursesForAdmin());
    }

}
