package com.example.web_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    public enum Status { PRESENT, LATE, ABSENT, EXCUSED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private ClassSession session;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    private LocalDateTime checkedAt;
    private Double studentLat;
    private Double studentLng;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PRESENT;
}
