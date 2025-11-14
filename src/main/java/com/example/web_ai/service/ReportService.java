package com.example.web_ai.service;

import com.example.web_ai.dto.request.ReportFilterRequest;
import com.example.web_ai.dto.response.*;
import com.example.web_ai.entity.Attendance;
import com.example.web_ai.entity.ClassSession;
import com.example.web_ai.entity.Course;
import com.example.web_ai.entity.User;
import com.example.web_ai.repository.AttendanceRepository;
import com.example.web_ai.repository.ClassSessionRepository;
import com.example.web_ai.repository.CourseRepository;
import com.example.web_ai.repository.UserRepository;
import com.example.web_ai.repository.projection.SessionAttendanceSummaryProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final AttendanceRepository attendanceRepository;
    private final ClassSessionRepository classSessionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Transactional(readOnly = true)
    public AttendanceReportResponse generateAttendanceReport(ReportFilterRequest filter, String generatedBy) {
        LocalDateTime fromDate = filter.getFromDate() != null ? filter.getFromDate() : LocalDateTime.now().minusMonths(1);
        LocalDateTime toDate = filter.getToDate() != null ? filter.getToDate() : LocalDateTime.now();

        // Build query based on filters
        List<Attendance> attendances;
        
        if (filter.getSessionId() != null) {
            // Get attendance for specific session
            attendances = attendanceRepository.findAllBySession_IdOrderByCheckedAtAsc(filter.getSessionId());
        } else if (filter.getStudentId() != null) {
            // Get attendance for specific student
            attendances = attendanceRepository.findStudentAttendanceHistory(
                filter.getStudentId(),
                filter.getCourseId(),
                fromDate,
                toDate
            );
        } else if (filter.getCourseId() != null) {
            // Get attendance for course
            List<ClassSession> sessions = classSessionRepository.findAllByCourse_IdAndStartTimeBetween(
                filter.getCourseId(),
                fromDate,
                toDate
            );
            attendances = sessions.stream()
                .flatMap(session -> attendanceRepository.findAllBySession_IdOrderByCheckedAtAsc(session.getId()).stream())
                .collect(Collectors.toList());
        } else {
            // Get all attendance in date range
            List<ClassSession> sessions = classSessionRepository.findAll().stream()
                .filter(s -> s.getStartTime().isAfter(fromDate) && s.getStartTime().isBefore(toDate))
                .collect(Collectors.toList());
            attendances = sessions.stream()
                .flatMap(session -> attendanceRepository.findAllBySession_IdOrderByCheckedAtAsc(session.getId()).stream())
                .collect(Collectors.toList());
        }

        // Calculate statistics
        long totalSessions = attendances.stream()
            .map(a -> a.getSession().getId())
            .distinct()
            .count();
        
        long totalStudents = attendances.stream()
            .map(a -> a.getStudent().getId())
            .distinct()
            .count();

        long presentCount = attendances.stream()
            .filter(a -> a.getStatus() == Attendance.Status.PRESENT)
            .count();
        
        long lateCount = attendances.stream()
            .filter(a -> a.getStatus() == Attendance.Status.LATE)
            .count();
        
        long absentCount = attendances.stream()
            .filter(a -> a.getStatus() == Attendance.Status.ABSENT)
            .count();
        
        long excusedCount = attendances.stream()
            .filter(a -> a.getStatus() == Attendance.Status.EXCUSED)
            .count();

        double attendanceRate = totalSessions > 0 && totalStudents > 0
            ? ((double) (presentCount + lateCount) / (totalSessions * totalStudents)) * 100
            : 0.0;

        // Get course info if available
        Course course = null;
        if (filter.getCourseId() != null) {
            course = courseRepository.findById(filter.getCourseId())
                .orElse(null);
        }

        // Get student info if available
        User student = null;
        if (filter.getStudentId() != null) {
            student = userRepository.findById(filter.getStudentId())
                .orElse(null);
        }

        // Build detail items if requested
        List<AttendanceReportResponse.AttendanceReportDetailItem> details = null;
        if (Boolean.TRUE.equals(filter.getIncludeDetails())) {
            details = attendances.stream()
                .map(a -> AttendanceReportResponse.AttendanceReportDetailItem.builder()
                    .sessionId(a.getSession().getId())
                    .sessionStartTime(a.getSession().getStartTime())
                    .sessionEndTime(a.getSession().getEndTime())
                    .roomName(a.getSession().getRoomName())
                    .studentId(a.getStudent().getId())
                    .studentName(a.getStudent().getFullName())
                    .studentEmail(a.getStudent().getEmail())
                    .status(a.getStatus() != null ? a.getStatus().name() : null)
                    .checkedAt(a.getCheckedAt())
                    .note(a.getNote())
                    .build())
                .collect(Collectors.toList());
        }

        String title = buildAttendanceReportTitle(filter, course, student);

        return AttendanceReportResponse.builder()
            .reportId(UUID.randomUUID())
            .reportType("ATTENDANCE")
            .title(title)
            .generatedAt(LocalDateTime.now())
            .generatedBy(generatedBy)
            .courseId(course != null ? course.getId() : null)
            .courseName(course != null ? course.getName() : null)
            .courseCode(course != null ? course.getCode() : null)
            .sessionId(filter.getSessionId())
            .studentId(student != null ? student.getId() : null)
            .studentName(student != null ? student.getFullName() : null)
            .fromDate(fromDate)
            .toDate(toDate)
            .totalSessions(totalSessions)
            .totalStudents(totalStudents)
            .presentCount(presentCount)
            .lateCount(lateCount)
            .absentCount(absentCount)
            .excusedCount(excusedCount)
            .attendanceRate(attendanceRate)
            .details(details)
            .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Transactional(readOnly = true)
    public SessionReportResponse generateSessionReport(ReportFilterRequest filter, String generatedBy) {
        LocalDateTime fromDate = filter.getFromDate() != null ? filter.getFromDate() : LocalDateTime.now().minusMonths(1);
        LocalDateTime toDate = filter.getToDate() != null ? filter.getToDate() : LocalDateTime.now();

        // Get sessions within range
        List<SessionAttendanceSummaryProjection> sessionSummaries = attendanceRepository.findSessionsWithinRange(
            fromDate,
            toDate,
            filter.getCourseId(),
            filter.getTeacherId()
        );

        // Calculate aggregate statistics
        long totalSessions = sessionSummaries.size();
        long totalStudents = sessionSummaries.stream()
            .mapToLong(SessionAttendanceSummaryProjection::getTotalEnrolled)
            .sum();
        long totalPresent = sessionSummaries.stream()
            .mapToLong(s -> s.getPresentCount() != null ? s.getPresentCount() : 0)
            .sum();
        long totalLate = sessionSummaries.stream()
            .mapToLong(s -> s.getLateCount() != null ? s.getLateCount() : 0)
            .sum();
        long totalAbsent = sessionSummaries.stream()
            .mapToLong(s -> s.getAbsentCount() != null ? s.getAbsentCount() : 0)
            .sum();
        long totalExcused = sessionSummaries.stream()
            .mapToLong(s -> s.getExcusedCount() != null ? s.getExcusedCount() : 0)
            .sum();

        double averageAttendanceRate = sessionSummaries.stream()
            .filter(s -> s.getTotalEnrolled() != null && s.getTotalEnrolled() > 0)
            .mapToDouble(s -> {
                long attended = (s.getPresentCount() != null ? s.getPresentCount() : 0) +
                               (s.getLateCount() != null ? s.getLateCount() : 0);
                return (double) attended / s.getTotalEnrolled() * 100;
            })
            .average()
            .orElse(0.0);

        // Get course info if available
        Course course = null;
        if (filter.getCourseId() != null) {
            course = courseRepository.findById(filter.getCourseId())
                .orElse(null);
        }

        // Get teacher info if available
        User teacher = null;
        if (filter.getTeacherId() != null) {
            teacher = userRepository.findById(filter.getTeacherId())
                .orElse(null);
        }

        // Build detail items
        List<SessionReportResponse.SessionReportDetailItem> sessionDetails = sessionSummaries.stream()
            .map(s -> {
                long attended = (s.getPresentCount() != null ? s.getPresentCount() : 0) +
                               (s.getLateCount() != null ? s.getLateCount() : 0);
                double rate = s.getTotalEnrolled() != null && s.getTotalEnrolled() > 0
                    ? (double) attended / s.getTotalEnrolled() * 100
                    : 0.0;

                return SessionReportResponse.SessionReportDetailItem.builder()
                    .sessionId(s.getSessionId())
                    .startTime(s.getStartTime())
                    .endTime(s.getEndTime())
                    .roomName(s.getRoomName())
                    .locked(s.getLocked() != null ? s.getLocked() : false)
                    .totalEnrolled(s.getTotalEnrolled())
                    .presentCount(s.getPresentCount())
                    .lateCount(s.getLateCount())
                    .absentCount(s.getAbsentCount())
                    .excusedCount(s.getExcusedCount())
                    .attendanceRate(rate)
                    .build();
            })
            .collect(Collectors.toList());

        String title = buildSessionReportTitle(filter, course, teacher);

        return SessionReportResponse.builder()
            .reportId(UUID.randomUUID())
            .reportType("SESSION")
            .title(title)
            .generatedAt(LocalDateTime.now())
            .generatedBy(generatedBy)
            .courseId(course != null ? course.getId() : null)
            .courseName(course != null ? course.getName() : null)
            .courseCode(course != null ? course.getCode() : null)
            .teacherId(teacher != null ? teacher.getId() : null)
            .teacherName(teacher != null ? teacher.getFullName() : null)
            .fromDate(fromDate)
            .toDate(toDate)
            .totalSessions(totalSessions)
            .totalStudents(totalStudents)
            .totalPresent(totalPresent)
            .totalLate(totalLate)
            .totalAbsent(totalAbsent)
            .totalExcused(totalExcused)
            .averageAttendanceRate(averageAttendanceRate)
            .sessions(sessionDetails)
            .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Transactional(readOnly = true)
    public Page<ReportListResponse> listReports(ReportFilterRequest filter, Pageable pageable) {
        // This is a simplified version - in a real system, you might store report metadata
        // For now, we'll return empty or generate on-the-fly
        // This endpoint can be used to list available report types or previously generated reports
        return Page.empty();
    }

    private String buildAttendanceReportTitle(ReportFilterRequest filter, Course course, User student) {
        StringBuilder title = new StringBuilder("Báo cáo điểm danh");
        if (course != null) {
            title.append(" - ").append(course.getCode()).append(": ").append(course.getName());
        }
        if (student != null) {
            title.append(" - SV: ").append(student.getFullName());
        }
        if (filter.getSessionId() != null) {
            title.append(" - Tiết học: ").append(filter.getSessionId().toString().substring(0, 8));
        }
        return title.toString();
    }

    private String buildSessionReportTitle(ReportFilterRequest filter, Course course, User teacher) {
        StringBuilder title = new StringBuilder("Báo cáo tiết học");
        if (course != null) {
            title.append(" - ").append(course.getCode()).append(": ").append(course.getName());
        }
        if (teacher != null) {
            title.append(" - GV: ").append(teacher.getFullName());
        }
        return title.toString();
    }
}

