package com.example.web_ai.entity;

import com.example.web_ai.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 36)
    UUID id;

    @Column(nullable = false)
    String fullName;

    @Column(nullable = false, unique = true, length = 100)
    String username;

    @Column(nullable = false)
    String password;

    @Column(nullable = false, unique = true, length = 200)
    String email;

    @Column(nullable = false, unique = true)
    String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    boolean active = true;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = true)
    Faculty faculty;
}
