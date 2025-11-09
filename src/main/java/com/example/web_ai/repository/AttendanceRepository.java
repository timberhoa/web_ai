package com.example.web_ai.repository;

import com.example.web_ai.entity.Attendance;
import com.example.web_ai.repository.projection.SessionAttendanceSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    @Query("SELECT " +
           "c.name as courseName, " +
           "c.code as courseCode, " +
           "cs.roomName as roomName, " +
           "(SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = c.id) as totalEnrolled, " +
           "COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) as presentCount, " +
           "COUNT(CASE WHEN a.status = 'LATE' THEN 1 END) as lateCount, " +
           "COUNT(CASE WHEN a.status = 'ABSENT' THEN 1 END) as absentCount, " +
           "COUNT(CASE WHEN a.status = 'EXCUSED' THEN 1 END) as excusedCount " +
           "FROM ClassSession cs " +
           "JOIN cs.course c " +
           "LEFT JOIN Attendance a ON a.session.id = cs.id " +
           "WHERE cs.id = :sessionId " +
           "GROUP BY c.id, c.name, c.code, cs.roomName")
    Object[] findAttendanceStatsBySessionId(@Param("sessionId") UUID sessionId);

    Optional<Attendance> findBySession_IdAndStudent_Id(UUID sessionId, UUID studentId);

    List<Attendance> findAllBySession_IdOrderByCheckedAtAsc(UUID sessionId);

    @Query("""
            SELECT cs.id as sessionId,
                   c.id as courseId,
                   c.name as courseName,
                   c.code as courseCode,
                   teacher.id as teacherId,
                   teacher.fullName as teacherName,
                   cs.roomName as roomName,
                   cs.startTime as startTime,
                   cs.endTime as endTime,
                   cs.locked as locked,
                   (SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = c.id) as totalEnrolled,
                   SUM(CASE WHEN a.status IN ('PRESENT','LATE','EXCUSED') THEN 1 ELSE 0 END) as totalMarked,
                   SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) as presentCount,
                   SUM(CASE WHEN a.status = 'LATE' THEN 1 ELSE 0 END) as lateCount,
                   SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) as absentCount,
                   SUM(CASE WHEN a.status = 'EXCUSED' THEN 1 ELSE 0 END) as excusedCount
            FROM ClassSession cs
            JOIN cs.course c
            LEFT JOIN c.teacher teacher
            LEFT JOIN Attendance a ON a.session = cs
            WHERE cs.startTime BETWEEN :from AND :to
              AND (:courseId IS NULL OR c.id = :courseId)
              AND (:teacherId IS NULL OR teacher.id = :teacherId)
            GROUP BY cs.id, c.id, c.name, c.code, teacher.id, teacher.fullName,
                     cs.roomName, cs.startTime, cs.endTime, cs.locked
            ORDER BY cs.startTime
            """)
    List<SessionAttendanceSummaryProjection> findSessionsWithinRange(@Param("from") LocalDateTime from,
                                                                     @Param("to") LocalDateTime to,
                                                                     @Param("courseId") UUID courseId,
                                                                     @Param("teacherId") UUID teacherId);

    @Query("""
            SELECT cs.id as sessionId,
                   c.id as courseId,
                   c.name as courseName,
                   c.code as courseCode,
                   teacher.id as teacherId,
                   teacher.fullName as teacherName,
                   cs.roomName as roomName,
                   cs.startTime as startTime,
                   cs.endTime as endTime,
                   cs.locked as locked,
                   (SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = c.id) as totalEnrolled,
                   SUM(CASE WHEN a.status IN ('PRESENT','LATE','EXCUSED') THEN 1 ELSE 0 END) as totalMarked,
                   SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) as presentCount,
                   SUM(CASE WHEN a.status = 'LATE' THEN 1 ELSE 0 END) as lateCount,
                   SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) as absentCount,
                   SUM(CASE WHEN a.status = 'EXCUSED' THEN 1 ELSE 0 END) as excusedCount
            FROM ClassSession cs
            JOIN cs.course c
            LEFT JOIN c.teacher teacher
            LEFT JOIN Attendance a ON a.session = cs
            WHERE cs.startTime <= :windowEnd
              AND cs.endTime >= :windowStart
              AND (:courseId IS NULL OR c.id = :courseId)
              AND (:teacherId IS NULL OR teacher.id = :teacherId)
            GROUP BY cs.id, c.id, c.name, c.code, teacher.id, teacher.fullName,
                     cs.roomName, cs.startTime, cs.endTime, cs.locked
            ORDER BY cs.startTime
            """)
    List<SessionAttendanceSummaryProjection> findLiveSessions(@Param("windowStart") LocalDateTime windowStart,
                                                              @Param("windowEnd") LocalDateTime windowEnd,
                                                              @Param("courseId") UUID courseId,
                                                              @Param("teacherId") UUID teacherId);

    @Query("""
            SELECT a FROM Attendance a
            JOIN a.session s
            JOIN s.course c
            WHERE a.student.id = :studentId
              AND (:courseId IS NULL OR c.id = :courseId)
              AND (:fromDate IS NULL OR s.startTime >= :fromDate)
              AND (:toDate IS NULL OR s.endTime <= :toDate)
            ORDER BY s.startTime DESC, a.checkedAt DESC
            """)
    Page<Attendance> findStudentAttendanceHistory(@Param("studentId") UUID studentId,
                                                  @Param("courseId") UUID courseId,
                                                  @Param("fromDate") LocalDateTime fromDate,
                                                  @Param("toDate") LocalDateTime toDate,
                                                  Pageable pageable);

    default List<Attendance> findStudentAttendanceHistory(UUID studentId,
                                                          UUID courseId,
                                                          LocalDateTime fromDate,
                                                          LocalDateTime toDate) {
        return findStudentAttendanceHistory(studentId, courseId, fromDate, toDate, Pageable.unpaged()).getContent();
    }

    long countByCheckedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatusIn(Collection<Attendance.Status> statuses);

    long countByStatusInAndCheckedAtBetween(Collection<Attendance.Status> statuses,
                                            LocalDateTime start,
                                            LocalDateTime end);

    long countBySession_Course_Teacher_IdAndCheckedAtBetween(UUID teacherId,
                                                             LocalDateTime start,
                                                             LocalDateTime end);

    long countBySession_Course_Teacher_IdAndCheckedAtBetweenAndStatus(UUID teacherId,
                                                                      LocalDateTime start,
                                                                      LocalDateTime end,
                                                                      Attendance.Status status);

    long countByStudent_IdAndCheckedAtBetween(UUID studentId, LocalDateTime start, LocalDateTime end);

    Optional<Attendance> findTopByStudent_IdOrderByCheckedAtDesc(UUID studentId);
}
