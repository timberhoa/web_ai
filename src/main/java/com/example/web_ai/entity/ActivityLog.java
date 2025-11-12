package com.example.web_ai.entity;

import com.example.web_ai.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "activity_logs",
        indexes = {
                @Index(name = "idx_activity_logs_username", columnList = "username"),
                @Index(name = "idx_activity_logs_role", columnList = "role"),
                @Index(name = "idx_activity_logs_path", columnList = "path"),
                @Index(name = "idx_activity_logs_occurred_at", columnList = "occurred_at")
        })
@Entity
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 36)
    UUID id;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "user_id", length = 36)
    UUID userId;

    @Column(length = 100)
    String username;

    @Enumerated(EnumType.STRING)
    Role role;

    @Column(length = 32, nullable = false)
    String method;

    @Column(nullable = false, length = 255)
    String path;

    @Column(length = 255)
    String query;

    @Column(length = 128, nullable = false)
    String action;

    @Column(length = 600)
    String message;

    @Column(length = 64)
    String ipAddress;

    @Column(length = 255)
    String userAgent;

    Integer status;

    @Column(name = "duration_ms")
    Long durationMs;

    @CreationTimestamp
    @Column(name = "occurred_at", nullable = false, updatable = false)
    LocalDateTime occurredAt;
}
