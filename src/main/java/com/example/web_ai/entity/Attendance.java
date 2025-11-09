package com.example.web_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "attendances",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_attendance_session_student",
                columnNames = {"session_id", "student_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Attendance {

    public enum Status {PRESENT, LATE, ABSENT, EXCUSED}
    @PrePersist
    protected void onCreate() {
        checkedAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 36)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    ClassSession session;

    @ManyToOne
    @JoinColumn(name = "student_id")
    User student;

    LocalDateTime checkedAt;
    Double studentLat;
    Double studentLng;
    @Column(length = 500)
    String note;

    @Enumerated(EnumType.STRING)
    Status status = Status.PRESENT;
}
