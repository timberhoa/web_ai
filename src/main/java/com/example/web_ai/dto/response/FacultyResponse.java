// FacultyResponse.java
package com.example.web_ai.dto.response;

import com.example.web_ai.entity.Faculty;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyResponse {
    private UUID id;
    private String code;
    private String name;
    private UUID teacherId;
    private String teacherName;

    public static FacultyResponse fromEntity(Faculty f) {
        return FacultyResponse.builder()
                .id(f.getId())
                .code(f.getCode())
                .name(f.getName())
                .teacherId(f.getHead() != null ? f.getHead().getId() : null)
                .teacherName(f.getHead() != null ? f.getHead().getFullName() : null)
                .build();
    }
}
