package com.example.web_ai.dto.request;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class FacultyRequest {
    String code;
    String name;
    // Removed teacherId since Faculty no longer has head field
}
