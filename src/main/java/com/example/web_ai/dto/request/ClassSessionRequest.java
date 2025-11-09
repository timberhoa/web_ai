package com.example.web_ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class ClassSessionRequest {

    @NotNull
    UUID courseId;

    @NotNull
    LocalDateTime startTime;

    @NotNull
    LocalDateTime endTime;

    @NotBlank
    String roomName;

    Double latitude;
    Double longitude;

    @Positive
    Double radiusMeters;
}
