package com.example.web_ai.dto.response;

import com.example.web_ai.entity.Course;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class CourseResponse {
    UUID id;
    String code;
    String name;
    UUID teacher_id;
    String teacher_name;
    Integer credits;
    String faculty_code;
    String faculty_name;

    public static CourseResponse fromEntity(Course c) {
        if (c == null) return null;
        
        return CourseResponse.builder()
                .id(c.getId())
                .code(c.getCode())
                .name(c.getName())
                .teacher_id(c.getTeacher() != null ? c.getTeacher().getId() : null)
                .teacher_name(c.getTeacher() != null ? c.getTeacher().getFullName() : null)
                .credits(c.getCredits())
                .faculty_code(c.getFaculty() != null ? c.getFaculty().getCode() : null)
                .faculty_name(c.getFaculty() != null ? c.getFaculty().getName() : null)
                .build();
    }
}
