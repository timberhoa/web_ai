package com.example.web_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "attendances")
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
    @Column(nullable = false)
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

    @Enumerated(EnumType.STRING)
    Status status = Status.PRESENT;
}
