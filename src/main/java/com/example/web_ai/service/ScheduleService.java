package com.example.web_ai.service;

import com.example.web_ai.dto.request.ClassSessionRequest;
import com.example.web_ai.dto.request.RecurringSessionRequest;
import com.example.web_ai.dto.response.ClassSessionResponse;
import com.example.web_ai.entity.ClassSession;
import com.example.web_ai.entity.Course;
import com.example.web_ai.exception.BadRequestException;
import com.example.web_ai.exception.NotFoundException;
import com.example.web_ai.mapper.ClassSessionMapper;
import com.example.web_ai.repository.ClassSessionRepository;
import com.example.web_ai.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ClassSessionRepository classSessionRepository;
    private final CourseRepository courseRepository;
    private final ClassSessionMapper classSessionMapper;

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ClassSessionResponse createSession(ClassSessionRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("COURSE_NOT_FOUND"));

        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateConflicts(course.getId(), null, request.getStartTime(), request.getEndTime(), request.getRoomName());

        ClassSession session = new ClassSession();
        session.setCourse(course);
        applyRequestToSession(session, request);

        ClassSession saved = classSessionRepository.save(session);
        return classSessionMapper.toResponse(saved);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public List<ClassSessionResponse> generateRecurringSessions(RecurringSessionRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("START_DATE_AFTER_END_DATE");
        }
        validateTimeRange(
                LocalDateTime.of(request.getStartDate(), request.getStartTime()),
                LocalDateTime.of(request.getStartDate(), request.getEndTime()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("COURSE_NOT_FOUND"));

        Set<DayOfWeek> dayOfWeeks = request.getDaysOfWeek();
        List<ClassSession> sessions = new ArrayList<>();

        LocalDate cursor = request.getStartDate();
        while (!cursor.isAfter(request.getEndDate())) {
            if (dayOfWeeks.contains(cursor.getDayOfWeek())) {
                LocalDateTime start = LocalDateTime.of(cursor, request.getStartTime());
                LocalDateTime end = LocalDateTime.of(cursor, request.getEndTime());

                validateConflicts(course.getId(), null, start, end, request.getRoomName());

                ClassSession session = new ClassSession();
                session.setCourse(course);
                session.setStartTime(start);
                session.setEndTime(end);
                session.setRoomName(request.getRoomName());
                session.setLatitude(request.getLatitude() != null ? request.getLatitude() : 0.0d);
                session.setLongitude(request.getLongitude() != null ? request.getLongitude() : 0.0d);
                session.setRadiusMeters(request.getRadiusMeters() != null ? request.getRadiusMeters() : 0.0d);
                sessions.add(session);
            }
            cursor = cursor.plusDays(1);
        }

        if (sessions.isEmpty()) {
            throw new BadRequestException("NO_SESSIONS_GENERATED");
        }

        List<ClassSession> saved = classSessionRepository.saveAll(sessions);
        return saved.stream().map(classSessionMapper::toResponse).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ClassSessionResponse updateSession(UUID sessionId, ClassSessionRequest request) {
        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));
        if (session.isLocked()) {
            throw new BadRequestException("SESSION_LOCKED");
        }

        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateConflicts(session.getCourse().getId(), session.getId(),
                request.getStartTime(), request.getEndTime(), request.getRoomName());

        applyRequestToSession(session, request);
        ClassSession saved = classSessionRepository.save(session);
        return classSessionMapper.toResponse(saved);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public void deleteSession(UUID sessionId) {
        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));
        if (session.isLocked()) {
            throw new BadRequestException("SESSION_LOCKED");
        }
        classSessionRepository.delete(session);
    }

    public ClassSessionResponse getSession(UUID sessionId) {
        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));
        return classSessionMapper.toResponse(session);
    }

    public Page<ClassSessionResponse> searchSessions(UUID courseId,
                                                     UUID teacherId,
                                                     LocalDateTime from,
                                                     LocalDateTime to,
                                                     Pageable pageable) {
        Specification<ClassSession> spec = Specification.where(null);

        if (courseId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("course").get("id"), courseId));
        }
        if (teacherId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("course").get("teacher").get("id"), teacherId));
        }
        if (from != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startTime"), from));
        }
        if (to != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("endTime"), to));
        }

        Page<ClassSession> result = classSessionRepository.findAll(spec, pageable);
        return result.map(classSessionMapper::toResponse);
    }

    private void applyRequestToSession(ClassSession session, ClassSessionRequest request) {
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setRoomName(request.getRoomName());
        if (request.getLatitude() != null) {
            session.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            session.setLongitude(request.getLongitude());
        }
        if (request.getRadiusMeters() != null) {
            session.setRadiusMeters(request.getRadiusMeters());
        }
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new BadRequestException("INVALID_TIME_RANGE");
        }
        if (!end.isAfter(start)) {
            throw new BadRequestException("END_TIME_MUST_BE_AFTER_START_TIME");
        }
    }

    private void validateConflicts(UUID courseId,
                                   UUID excludeSessionId,
                                   LocalDateTime start,
                                   LocalDateTime end,
                                   String roomName) {
        if (classSessionRepository.hasOverlappingSessionForCourse(courseId, excludeSessionId, start, end)) {
            throw new BadRequestException("COURSE_SESSION_CONFLICT");
        }
        if (classSessionRepository.hasOverlappingSessionForRoom(roomName, excludeSessionId, start, end)) {
            throw new BadRequestException("ROOM_SESSION_CONFLICT");
        }
    }
}
