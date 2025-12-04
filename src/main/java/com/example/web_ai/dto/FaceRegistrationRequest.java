package com.example.web_ai.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Data
public class FaceRegistrationRequest {
    private List<MultipartFile> images; // 3-5 images
    private UUID userId;
}
