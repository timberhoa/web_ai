package com.example.web_ai.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "faculties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 36)
    UUID id;

    @Column(nullable = false, unique = true, length = 50)
    String code;

    @Column(nullable = false, length = 150)
    String name;

    // Removed head field to avoid circular dependency
    // Head information can be managed through User role or separate table if needed

    @OneToMany(mappedBy = "faculty")
    List<User> users;

    @OneToMany(mappedBy = "faculty")
    List<Course> courses;
}
