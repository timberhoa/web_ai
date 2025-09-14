package com.example.web_ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    UUID id;

    @Column(nullable=false, unique=true, length=30)
    private String code;

    @Column(nullable=false, length=150)
    private String name;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    private Integer credits;
}
