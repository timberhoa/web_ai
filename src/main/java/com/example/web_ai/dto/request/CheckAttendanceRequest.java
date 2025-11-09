package com.example.web_ai.dto.request;

import com.example.web_ai.entity.Attendance;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.util.UUID;
import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class CheckAttendanceRequest {
    Attendance.Status status;
    UUID student_id;
    UUID session_id;
    Double studentLat;
    Double studentLng;
    String note;
}
