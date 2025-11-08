package com.example.web_ai.controller;

import com.example.web_ai.dto.request.CourseRequest;
import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Page<CourseResponse>> listCourses(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(courseService.listCourses(pageable));
    }

    @GetMapping("/by-faculty/{faculty_code}")
    public ResponseEntity<Page<CourseResponse>> getCourseByFacultyCode(
            @PathVariable String faculty_code,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(courseService.getListCourseByFaculty(faculty_code, pageable));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<Page<CourseResponse>> getAllCoursesForAdmin(
            @PageableDefault(size = 15, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCoursesForAdmin(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CourseResponse>> searchCoursesByName(
            @RequestParam("name") String name,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(courseService.searchCoursesByName(name, pageable));
    }

}
