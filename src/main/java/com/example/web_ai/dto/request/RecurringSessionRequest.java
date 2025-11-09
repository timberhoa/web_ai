package com.example.web_ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class RecurringSessionRequest {

    @NotNull
    UUID courseId;

    @NotNull
    LocalDate startDate;

    @NotNull
    LocalDate endDate;

    @NotNull
    LocalTime startTime;

    @NotNull
    LocalTime endTime;

    @NotEmpty
    Set<DayOfWeek> daysOfWeek;

    @NotBlank
    String roomName;

    Double latitude;
    Double longitude;

    @Positive
    Double radiusMeters;
}
