package com.example.web_ai.dto.response;

import com.example.web_ai.enums.Role;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class ActivityLogResponse {
    UUID id;
    UUID userId;
    String username;
    Role role;
    String action;
    String method;
    String path;
    String query;
    Integer status;
    String ipAddress;
    String userAgent;
    String message;
    Long durationMs;
    LocalDateTime occurredAt;
}
