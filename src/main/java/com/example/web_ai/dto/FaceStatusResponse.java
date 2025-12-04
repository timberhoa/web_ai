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
public class FaceStatusResponse {
    private Boolean isRegistered;
    private LocalDateTime registeredAt;
    private Float qualityScore;
}
