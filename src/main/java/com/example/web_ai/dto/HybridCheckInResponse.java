package com.example.web_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HybridCheckInResponse {
    private Boolean success;
    private Boolean isMatch;
    private Float confidence;
    private String status; // PRESENT, LATE
    private String message;
    private UUID attendanceId;
    private String checkInType; // "FACE_AND_LOCATION" or "LOCATION_ONLY"
}
