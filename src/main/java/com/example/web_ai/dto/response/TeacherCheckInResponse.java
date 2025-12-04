package com.example.web_ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCheckInResponse {
    private Boolean success;
    private UUID studentId;
    private String studentName;
    private Float confidence;
    private String status;
    private String message;
}
