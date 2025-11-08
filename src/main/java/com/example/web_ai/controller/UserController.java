package com.example.web_ai.controller;

import com.example.web_ai.dto.request.ResetPassword;
import com.example.web_ai.dto.request.UpdateProfileMeRequest;
import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.GradeResponse;
import com.example.web_ai.dto.response.UploadImageResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.entity.Image;
import com.example.web_ai.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/getUserById")
    public UserResponse getUserByUsername(@RequestParam UUID id) {
        return userService.getUserById(id);
    }

    // @PreAuthorize("hasRole('TEACHER','STUDENT','ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication) {
        String username = authentication.getName();
        log.info("🔹 Username from Authentication: {}", username);
        UserResponse res = userService.getUserByUsername(username);

        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(Authentication authentication, UpdateProfileMeRequest req) {
        String username = authentication.getName();
        log.info("Req {}", req);
        UserResponse res = userService.updateProfileMe(username, req);

        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id,
            @RequestBody UserRequest req) {
        return ResponseEntity.ok(userService.updateUser(id, req));
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT','TEACHER')")
    @PutMapping("/updatePassword/{id}")
    public ResponseEntity<String> updatePassword(@PathVariable UUID id, @RequestBody ResetPassword req) {
        userService.updatePassword(id, req);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping(value = "grades")
    public ResponseEntity<GradeResponse> getAllGrade(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userIdString = jwt.getClaim("id");

        UUID userId = UUID.fromString(userIdString);

        return ResponseEntity.ok(userService.getALlGrade(userId));
    }

    @GetMapping(value = "/image/{imageId}")
    public ResponseEntity<Image> getImage(@PathVariable UUID imageId) {
        Image image = userService.getImage(imageId);
        return ResponseEntity.ok(image);
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadImageResponse> uploadImage(
            @RequestParam("file") MultipartFile file, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userIdString = jwt.getClaim("id");

        log.info("🔹 userIdString: {}", userIdString);

        UUID userId = UUID.fromString(userIdString);
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(UploadImageResponse.builder()
                        .message("File is empty").id(userId).build());
            }

            String contentType = file.getContentType();
            if (!contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(UploadImageResponse.builder()
                        .message("Content type not allowed").id(userId).build());
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(UploadImageResponse.builder()
                        .message("File exceeds 5MP").id(userId).build());
            }

            userService.uploadImage(userId, file);
            return ResponseEntity.ok().body(UploadImageResponse.builder()
                    .message("Upload successfully").id(userId).build());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(UploadImageResponse.builder()
                    .message("Failed to upload image").id(userId).build());
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/students")
    public ResponseEntity<Page<UserResponse>> getAllStudents(
            @PageableDefault(size = 15, sort = "fullName") Pageable pageable) {
        Page<UserResponse> students = userService.getAllStudents(pageable);
        return ResponseEntity.ok(students);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam("q") String searchTerm,
            @RequestParam(value = "role", required = false) String role,
            @PageableDefault(size = 10, sort = "fullName") Pageable pageable) {
        Page<UserResponse> users = userService.searchUsers(searchTerm, role, pageable);
        return ResponseEntity.ok(users);
    }
}
