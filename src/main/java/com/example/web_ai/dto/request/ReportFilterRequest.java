package com.example.web_ai.dto.request;

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
public class ReportFilterRequest {
    String reportType; // "ATTENDANCE", "SESSION", "COURSE", "STUDENT"
    UUID courseId;
    UUID sessionId;
    UUID studentId;
    UUID teacherId;
    LocalDateTime fromDate;
    LocalDateTime toDate;
    Boolean includeDetails; // Include detailed records in response
}

