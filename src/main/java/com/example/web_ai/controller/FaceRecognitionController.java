package com.example.web_ai.controller;

import com.example.web_ai.dto.FaceRegistrationResponse;
import com.example.web_ai.dto.FaceStatusResponse;
import com.example.web_ai.service.FaceRecognitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.oauth2.jwt.Jwt;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Face Recognition", description = "Face recognition management endpoints")
public class FaceRecognitionController {

    private final FaceRecognitionService faceRecognitionService;

    @Operation(summary = "Register face for student (Admin only)")
    @PostMapping(value = "/admin/user/{userId}/face/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FaceRegistrationResponse> registerFace(
            @PathVariable UUID userId,
            @RequestParam("images") List<MultipartFile> images) {
        return ResponseEntity.ok(faceRecognitionService.registerFace(userId, images));
    }

    @Operation(summary = "Check face registration status (Current User)")
    @GetMapping("/user/face/status")
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public ResponseEntity<FaceStatusResponse> getFaceStatus(Authentication auth) {
        Jwt jwt = (Jwt) auth.getPrincipal();
        UUID userId = UUID.fromString(jwt.getClaim("id"));
        return ResponseEntity.ok(faceRecognitionService.getFaceStatus(userId));
    }

    @Operation(summary = "Check face registration status by User ID (Admin only)")
    @GetMapping("/admin/user/{userId}/face/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FaceStatusResponse> getFaceStatusByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(faceRecognitionService.getFaceStatus(userId));
    }

    @Operation(summary = "Delete face data (Admin only)")
    @DeleteMapping("/admin/user/{userId}/face")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFace(@PathVariable UUID userId) {
        faceRecognitionService.deleteFaceData(userId);
        return ResponseEntity.noContent().build();
    }
}
