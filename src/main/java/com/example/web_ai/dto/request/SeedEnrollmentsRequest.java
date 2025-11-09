package com.example.web_ai.dto.request;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class SeedEnrollmentsRequest {
    UUID courseId;
    UUID facultyId; // optional: limit to students of a faculty
    Integer limit;  // optional: max number of students to enroll
}

