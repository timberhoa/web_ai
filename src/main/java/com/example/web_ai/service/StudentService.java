package com.example.web_ai.service;

import com.example.web_ai.dto.response.ClassSessionResponse;
import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.dto.response.StudentAttendanceHistoryResponse;
import com.example.web_ai.dto.response.StudentAttendanceStatsResponse;
import com.example.web_ai.entity.Attendance;
import com.example.web_ai.entity.ClassSession;
import com.example.web_ai.entity.Course;
import com.example.web_ai.entity.Enrollment;
import com.example.web_ai.repository.AttendanceRepository;
import com.example.web_ai.repository.ClassSessionRepository;
import com.example.web_ai.repository.EnrollmentRepository;
import com.example.web_ai.mapper.ClassSessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ClassSessionRepository classSessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final ClassSessionMapper classSessionMapper;

    public Page<ClassSessionResponse> getMySessions(UUID studentId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {
        List<UUID> courseIds = enrollmentRepository.findByStudent_Id(studentId).stream()
                .map(enrollment -> enrollment.getCourse().getId())
                .distinct()
                .collect(Collectors.toList());

        if (courseIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Specification<ClassSession> spec = (root, query, cb) -> root.get("course").get("id").in(courseIds);
        if (from != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startTime"), from));
        }
        if (to != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("endTime"), to));
        }

        Page<ClassSession> sessions = classSessionRepository.findAll(spec, pageable);
        return sessions.map(classSessionMapper::toResponse);
    }

    public List<CourseResponse> getMyCourses(UUID studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudent_IdWithCourseDetails(studentId);
        Map<UUID, CourseResponse> distinctCourses = new LinkedHashMap<>();
        enrollments.forEach(enrollment -> {
            CourseResponse response = CourseResponse.fromEntity(enrollment.getCourse());
            distinctCourses.put(response.getId(), response);
        });
        return distinctCourses.values().stream().collect(Collectors.toList());
    }

    public Page<StudentAttendanceHistoryResponse> getMyAttendanceHistory(UUID studentId,
            UUID courseId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {
        Pageable sanitized = sanitizeHistoryPageable(pageable);
        Page<Attendance> attendancePage = attendanceRepository
                .findStudentAttendanceHistory(studentId, courseId, from, to, sanitized);
        return attendancePage.map(this::toHistoryResponse);
    }

    public List<StudentAttendanceStatsResponse> getMyAttendanceStats(UUID studentId,
            UUID courseId,
            LocalDateTime from,
            LocalDateTime to) {
        List<Attendance> records = attendanceRepository
                .findStudentAttendanceHistory(studentId, courseId, from, to);
        if (records.isEmpty()) {
            return Collections.emptyList();
        }

        Map<UUID, List<Attendance>> grouped = records.stream()
                .collect(Collectors.groupingBy(a -> a.getSession().getCourse().getId()));

        return grouped.values().stream()
                .map(this::toStatsResponse)
                .sorted((a, b) -> a.getCourseName().compareToIgnoreCase(b.getCourseName()))
                .collect(Collectors.toList());
    }

    StudentAttendanceHistoryResponse toHistoryResponse(Attendance attendance) {
        Course course = attendance.getSession().getCourse();
        return StudentAttendanceHistoryResponse.builder()
                .attendanceId(attendance.getId())
                .sessionId(attendance.getSession().getId())
                .courseId(course.getId())
                .courseName(course.getName())
                .courseCode(course.getCode())
                .startTime(attendance.getSession().getStartTime())
                .endTime(attendance.getSession().getEndTime())
                .roomName(attendance.getSession().getRoomName())
                .status(attendance.getStatus())
                .checkedAt(attendance.getCheckedAt())
                .note(attendance.getNote())
                .build();
    }

    private StudentAttendanceStatsResponse toStatsResponse(List<Attendance> records) {
        Attendance sample = records.get(0);
        Course course = sample.getSession().getCourse();

        long totalSessions = records.size();
        long present = records.stream().filter(a -> a.getStatus() == Attendance.Status.PRESENT).count();
        long late = records.stream().filter(a -> a.getStatus() == Attendance.Status.LATE).count();
        long excused = records.stream().filter(a -> a.getStatus() == Attendance.Status.EXCUSED).count();
        long absent = records.stream().filter(a -> a.getStatus() == Attendance.Status.ABSENT).count();
        long attended = present + late + excused;
        double attendanceRate = totalSessions > 0 ? Math.round(((double) attended / totalSessions) * 10000.0) / 100.0
                : 0.0;

        return StudentAttendanceStatsResponse.builder()
                .courseId(course.getId())
                .courseName(course.getName())
                .courseCode(course.getCode())
                .totalSessions(totalSessions)
                .attendedSessions(attended)
                .presentCount(present)
                .lateCount(late)
                .excusedCount(excused)
                .absentCount(absent)
                .attendanceRate(attendanceRate)
                .build();
    }

    public List<ClassSessionResponse> getSessionsForRange(UUID studentId,
            LocalDateTime from,
            LocalDateTime to,
            int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Order.asc("startTime")));
        return getMySessions(studentId, from, to, pageable).getContent();
    }

    private Pageable sanitizeHistoryPageable(Pageable pageable) {
        List<Sort.Order> orders = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            String property = order.getProperty();
            if ("startTime".equalsIgnoreCase(property)) {
                orders.add(new Sort.Order(order.getDirection(), "session.startTime"));
            } else if ("courseName".equalsIgnoreCase(property)) {
                orders.add(new Sort.Order(order.getDirection(), "session.course.name"));
            } else if ("checkedAt".equalsIgnoreCase(property) ||
                    "status".equalsIgnoreCase(property)) {
                orders.add(new Sort.Order(order.getDirection(), property));
            }
        });

        Sort fallback = Sort.by(Sort.Order.desc("session.startTime"), Sort.Order.desc("checkedAt"));
        Sort sort = orders.isEmpty() ? fallback : Sort.by(orders);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
