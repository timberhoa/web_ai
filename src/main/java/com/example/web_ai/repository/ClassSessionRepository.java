package com.example.web_ai.repository;

import com.example.web_ai.entity.ClassSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClassSessionRepository extends JpaRepository<ClassSession, UUID>, JpaSpecificationExecutor<ClassSession> {
    Optional<ClassSession> findClassSessionById(UUID id);

    List<ClassSession> findAllByCourse_IdAndStartTimeBetween(UUID courseId, LocalDateTime start, LocalDateTime end);

    long countByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    long countByCourse_Teacher_IdAndStartTimeBetween(UUID teacherId, LocalDateTime start, LocalDateTime end);

    List<ClassSession> findTop5ByCourse_Teacher_IdAndStartTimeGreaterThanEqualOrderByStartTimeAsc(UUID teacherId,
                                                                                                   LocalDateTime start);

    @Query("""
            SELECT COUNT(cs) > 0 FROM ClassSession cs
            WHERE cs.course.id = :courseId
              AND (:excludeId IS NULL OR cs.id <> :excludeId)
              AND cs.startTime < :endTime
              AND cs.endTime > :startTime
            """)
    boolean hasOverlappingSessionForCourse(@Param("courseId") UUID courseId,
                                           @Param("excludeId") UUID excludeId,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    @Query("""
            SELECT COUNT(cs) > 0 FROM ClassSession cs
            WHERE UPPER(cs.roomName) = UPPER(:roomName)
              AND (:excludeId IS NULL OR cs.id <> :excludeId)
              AND cs.startTime < :endTime
              AND cs.endTime > :startTime
            """)
    boolean hasOverlappingSessionForRoom(@Param("roomName") String roomName,
                                         @Param("excludeId") UUID excludeId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);
}
