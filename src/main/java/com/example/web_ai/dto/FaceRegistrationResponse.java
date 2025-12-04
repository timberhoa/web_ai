package com.example.web_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceRegistrationResponse {
    private Boolean success;
    private String message;
    private Integer imagesProcessed;
    private Float qualityScore;
    private LocalDateTime registeredAt;
}
