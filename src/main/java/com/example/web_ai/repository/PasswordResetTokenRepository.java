package com.example.web_ai.repository;

import com.example.web_ai.entity.PasswordResetToken;
import com.example.web_ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.user = :user")
    void deleteByUser(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiryDate < :date")
    void deleteByExpiryDateBefore(LocalDateTime date);
}
