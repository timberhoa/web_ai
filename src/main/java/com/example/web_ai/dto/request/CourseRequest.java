package com.example.web_ai.dto.request;

import com.example.web_ai.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class CourseRequest {
    String code;

    String name;

    UUID teacher_id;

    Integer credits;

    UUID faculty_id;

}
