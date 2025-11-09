package com.example.web_ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class EnrollmentBulkResponse {
    UUID courseId;
    int addedCount;
    int skippedCount;
    List<UUID> addedStudentIds;
    List<UUID> skippedStudentIds;
}

