package com.example.web_ai.filter;

import com.example.web_ai.entity.ActivityLog;
import com.example.web_ai.enums.Role;
import com.example.web_ai.service.ActivityLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class ActivityLoggingFilter extends OncePerRequestFilter {

    private static final List<String> IGNORED_PATH_PREFIXES = java.util.Arrays.asList(
            "/swagger", "/v3/api-docs", "/error", "/actuator", "/favicon", "/h2-console");

    private final ActivityLogService activityLogService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        return IGNORED_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        long startedAt = System.currentTimeMillis();
        int status = HttpStatus.OK.value();
        String message = "OK";

        try {
            filterChain.doFilter(request, response);
            status = response.getStatus();
        } catch (Exception ex) {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            message = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startedAt;
            ActivityLog logEntry = buildLogEntry(request, status, message, duration);
            activityLogService.saveLog(logEntry);
        }
    }

    private ActivityLog buildLogEntry(HttpServletRequest request,
            int status,
            String message,
            long durationMs) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = null;
        Role role = null;
        String username = "anonymous";

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            username = authentication.getName();
            Object principal = authentication.getPrincipal();
            if (principal instanceof Jwt jwt) {
                Object rawId = jwt.getClaim("id");
                if (rawId != null) {
                    try {
                        userId = UUID.fromString(String.valueOf(rawId));
                    } catch (IllegalArgumentException e) {
                        log.debug("Unable to parse user id from JWT: {}", rawId);
                    }
                }
                Object rawRole = jwt.getClaim("role");
                if (rawRole instanceof String roleClaim) {
                    try {
                        role = Role.valueOf(roleClaim);
                    } catch (IllegalArgumentException e) {
                        log.debug("Unable to parse role from JWT: {}", roleClaim);
                    }
                }
            }
        }

        String method = request.getMethod();
        String path = request.getRequestURI();

        return ActivityLog.builder()
                .userId(userId)
                .username(username)
                .role(role)
                .method(method)
                .path(path)
                .query(request.getQueryString())
                .action(method + " " + path)
                .message(message)
                .status(status)
                .ipAddress(resolveClientIp(request))
                .userAgent(truncate(request.getHeader("User-Agent"), 250))
                .durationMs(durationMs)
                .build();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String truncate(String value, int max) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }
}
