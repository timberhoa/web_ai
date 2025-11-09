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
public class SessionRosterItemResponse {
    UUID studentId;
    String studentName;
    String studentEmail;
    boolean marked; // whether attendance record exists
    String status;  // PRESENT | LATE | ABSENT | EXCUSED | null
    LocalDateTime checkedAt;
    Double studentLat;
    Double studentLng;
    String note;
}

