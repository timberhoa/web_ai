package com.example.web_ai.dto.request;

import com.example.web_ai.enums.Role;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ActivityLogQueryRequest {

    private String username;
    private Role role;
    private String method;
    private Integer status;
    private String path;
    private String action;
    private String keyword;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime to;
}
