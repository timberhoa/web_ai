package com.example.web_ai.repository.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SessionAttendanceSummaryProjection {
    UUID getSessionId();

    UUID getCourseId();

    String getCourseName();

    String getCourseCode();

    UUID getTeacherId();

    String getTeacherName();

    String getRoomName();

    LocalDateTime getStartTime();

    LocalDateTime getEndTime();

    Boolean getLocked();

    Long getTotalEnrolled();

    Long getTotalMarked();

    Long getPresentCount();

    Long getLateCount();

    Long getAbsentCount();

    Long getExcusedCount();
}
