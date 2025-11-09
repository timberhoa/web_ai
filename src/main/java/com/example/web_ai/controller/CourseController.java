package com.example.web_ai.controller;

import com.example.web_ai.dto.request.CourseRequest;
import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.service.CourseService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Course", description = "Manage courses: CRUD and queries")
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/")
    @Operation(summary = "Create course", description = "Add a new course and optionally assign teacher/faculty.")
    public ResponseEntity<CourseResponse> createCourse(@RequestBody @Valid CourseRequest req) {
        return ResponseEntity.ok(courseService.addCourse(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course", description = "Edit course metadata or change assigned teacher.")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable UUID id,
                                                       @RequestBody @Valid CourseRequest req) {
        return ResponseEntity.ok(courseService.updateCourse(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course", description = "Remove a course by ID.")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course", description = "Fetch a single course detail by ID.")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable UUID id) {
        return ResponseEntity.ok(courseService.getCourse(id));
    }

    @GetMapping
    @Operation(summary = "List courses", description = "Paged list of courses for general view.")
    public ResponseEntity<Page<CourseResponse>> listCourses(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(courseService.listCourses(pageable));
    }

    @GetMapping("/by-faculty/{faculty_code}")
    @Operation(summary = "Courses by faculty", description = "Paged list filtered by faculty code.")
    public ResponseEntity<Page<CourseResponse>> getCourseByFacultyCode(
            @PathVariable String faculty_code,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(courseService.getListCourseByFaculty(faculty_code, pageable));
    }

    @GetMapping("/admin/all")
    @Operation(summary = "Admin list courses", description = "Admin view of all courses (paged).")
    public ResponseEntity<Page<CourseResponse>> getAllCoursesForAdmin(
            @PageableDefault(size = 15, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCoursesForAdmin(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search courses", description = "Search courses by partial name (paged).")
    public ResponseEntity<Page<CourseResponse>> searchCoursesByName(
            @RequestParam("name") String name,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(courseService.searchCoursesByName(name, pageable));
    }

}
