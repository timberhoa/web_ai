package com.example.web_ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ReportListResponse {
    UUID reportId;
    String reportType; // "ATTENDANCE", "SESSION", "COURSE", "STUDENT"
    String title;
    String description;
    UUID courseId;
    String courseName;
    String courseCode;
    UUID sessionId;
    LocalDateTime startDate;
    LocalDateTime endDate;
    LocalDateTime generatedAt;
    String generatedBy;
    Long totalRecords;
}

