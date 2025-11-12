package com.example.web_ai.mapper;

import com.example.web_ai.dto.response.ActivityLogResponse;
import com.example.web_ai.entity.ActivityLog;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

    public ActivityLogResponse toResponse(ActivityLog entity) {
        if (entity == null) {
            return null;
        }
        return ActivityLogResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .role(entity.getRole())
                .action(entity.getAction())
                .method(entity.getMethod())
                .path(entity.getPath())
                .query(entity.getQuery())
                .status(entity.getStatus())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .message(entity.getMessage())
                .durationMs(entity.getDurationMs())
                .occurredAt(entity.getOccurredAt())
                .build();
    }
}
