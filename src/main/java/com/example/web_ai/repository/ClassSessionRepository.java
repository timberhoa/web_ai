package com.example.web_ai.repository;

import com.example.web_ai.entity.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClassSessionRepository extends JpaRepository<ClassSession, UUID> {
    Optional<ClassSession> findClassSessionById(UUID id);
}
