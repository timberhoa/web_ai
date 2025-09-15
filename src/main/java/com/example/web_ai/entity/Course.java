package com.example.web_ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    UUID id;

    @Column(nullable = false, unique = true, length = 30)
    String code;

    @Column(nullable = false, length = 150)
    String name;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    User teacher;

    Integer credits;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    Faculty faculty;


}
