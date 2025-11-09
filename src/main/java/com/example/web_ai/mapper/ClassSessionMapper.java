package com.example.web_ai.mapper;

import com.example.web_ai.dto.response.ClassSessionResponse;
import com.example.web_ai.entity.ClassSession;
import org.springframework.stereotype.Component;

@Component
public class ClassSessionMapper {

    public ClassSessionResponse toResponse(ClassSession session) {
        if (session == null) return null;
        var course = session.getCourse();
        var teacher = course != null ? course.getTeacher() : null;

        return ClassSessionResponse.builder()
                .sessionId(session.getId())
                .courseId(course != null ? course.getId() : null)
                .courseName(course != null ? course.getName() : null)
                .courseCode(course != null ? course.getCode() : null)
                .teacherId(teacher != null ? teacher.getId() : null)
                .teacherName(teacher != null ? teacher.getFullName() : null)
                .roomName(session.getRoomName())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .latitude(session.getLatitude())
                .longitude(session.getLongitude())
                .radiusMeters(session.getRadiusMeters())
                .locked(session.isLocked())
                .build();
    }
}
