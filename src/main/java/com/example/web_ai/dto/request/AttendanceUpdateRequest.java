package com.example.web_ai.dto.request;

import com.example.web_ai.entity.Attendance;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.Size;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class AttendanceUpdateRequest {

    Attendance.Status status;

    Double studentLat;
    Double studentLng;

    @Size(max = 500)
    String note;
}
