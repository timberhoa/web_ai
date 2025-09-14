package com.example.web_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "class_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String roomName;
    private double latitude;
    private double longitude;
    private double radiusMeters;
}
