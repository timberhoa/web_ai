package com.example.web_ai.repository;

import com.example.web_ai.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AttendanceRespository extends JpaRepository<Attendance, Long> {

    @Query("SELECT " +
           "cs.id, " +
           "c.name, " +
           "c.code, " +
           "cs.roomName, " +
           "(SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = c.id), " +
           "SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'LATE' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'EXCUSED' THEN 1 ELSE 0 END) " +
           "FROM ClassSession cs " +
           "JOIN cs.course c " +
           "LEFT JOIN Attendance a ON a.session.id = cs.id " +
           "WHERE cs.id = :sessionId " +
           "GROUP BY cs.id, c.name, c.code, cs.roomName")
    Object[] findAttendanceStatsBySessionId(@Param("sessionId") UUID sessionId);
}
