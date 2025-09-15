package com.example.web_ai.repository;

import com.example.web_ai.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FacultyRepository extends JpaRepository<Faculty, UUID> {
    boolean existsByCode(String code);
    boolean existsById(UUID id);
    Optional<Faculty> findByCode(String code);
    boolean existsByCodeAndIdNot(String code, UUID id);
}
