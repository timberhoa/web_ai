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
    // Removed head/teacher fields since Faculty no longer has head

    public static FacultyResponse fromEntity(Faculty f) {
        if (f == null) return null;
        
        return FacultyResponse.builder()
                .id(f.getId())
                .code(f.getCode())
                .name(f.getName())
                .build();
    }
}
