package com.example.web_ai.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class SelfCheckAttendanceRequest {
    @NotNull
    UUID session_id;

    // Optional geo coordinates captured on the device for geo-fence validation
    Double studentLat;
    Double studentLng;

    // Optional captured image reference (for audit / future FR integration)
    UUID imageId;
}

