package com.example.web_ai.dto.request;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class EnrollStudentsRequest {
    UUID courseId;
    List<UUID> studentIds;
}

