package com.example.web_ai.controller;

import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/courses")
    public ResponseEntity<Page<CourseResponse>> getAllCoursesByStudent(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "name") Pageable pageable){
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userIdString = jwt.getClaim("id");

        log.info("🔹 userIdString: {}", userIdString);

        UUID userId = UUID.fromString(userIdString);
        return ResponseEntity.ok(enrollmentService.getAllCoursesByStudentId(userId, pageable));

    }

}
