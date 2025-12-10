package com.example.web_ai.service;

import com.example.web_ai.dto.HybridCheckInResponse;
import com.example.web_ai.dto.request.AttendanceUpdateRequest;
import com.example.web_ai.dto.request.CheckAttendanceRequest;
import com.example.web_ai.dto.request.SelfCheckAttendanceRequest;
import com.example.web_ai.dto.response.*;
import com.example.web_ai.entity.Attendance;
import com.example.web_ai.entity.ClassSession;
import com.example.web_ai.entity.Enrollment;
import com.example.web_ai.entity.User;
import com.example.web_ai.exception.BadRequestException;
import com.example.web_ai.exception.NotFoundException;
import com.example.web_ai.mapper.AttendanceMapper;
import com.example.web_ai.mapper.ClassSessionMapper;
import com.example.web_ai.repository.AttendanceRepository;
import com.example.web_ai.repository.ClassSessionRepository;
import com.example.web_ai.repository.EnrollmentRepository;
import com.example.web_ai.repository.UserRepository;
import com.example.web_ai.repository.projection.SessionAttendanceSummaryProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final ClassSessionRepository classSessionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceMapper attendanceMapper;
    private final ClassSessionMapper classSessionMapper;
    private final FaceRecognitionService faceRecognitionService;

    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public CheckAttendanceResponse checkAttendance(CheckAttendanceRequest request) {
        ClassSession session = classSessionRepository.findClassSessionById(request.getSession_id())
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));
        ensureSessionEditable(session);

        User student = userRepository.findUserById(request.getStudent_id())
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        Attendance attendance = attendanceRepository
                .findBySession_IdAndStudent_Id(session.getId(), student.getId())
                .orElseGet(() -> {
                    Attendance entity = attendanceMapper.toEntity(request);
                    entity.setSession(session);
                    entity.setStudent(student);
                    return entity;
                });

        if (request.getStatus() != null) {
            attendance.setStatus(request.getStatus());
        }
        if (request.getStudentLat() != null) {
            attendance.setStudentLat(request.getStudentLat());
        }
        if (request.getStudentLng() != null) {
            attendance.setStudentLng(request.getStudentLng());
        }
        if (request.getNote() != null) {
            attendance.setNote(request.getNote());
        }
        attendance.setCheckedAt(LocalDateTime.now());

        Attendance saved = attendanceRepository.save(attendance);

        CheckAttendanceResponse response = attendanceMapper.toResponse(saved);
        response.setMessage("Check attendance successfully");
        response.setStudentName(student.getFullName());
        return response;
    }

    @PreAuthorize("hasRole('STUDENT')")
    public CheckAttendanceResponse selfCheck(UUID studentId, SelfCheckAttendanceRequest request) {
        ClassSession session = classSessionRepository.findClassSessionById(request.getSession_id())
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));
        ensureSessionEditable(session);

        // Must be enrolled to the course
        if (!enrollmentRepository.existsByCourse_IdAndStudent_Id(session.getCourse().getId(), studentId)) {
            throw new BadRequestException("NOT_ENROLLED");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime early = session.getStartTime().minusMinutes(15);
        LocalDateTime late = session.getEndTime().plusMinutes(15);
        if (now.isBefore(early) || now.isAfter(late)) {
            throw new BadRequestException("SESSION_NOT_ACTIVE");
        }

        // Validate geo-fence when configured
        if (session.getRadiusMeters() > 0) {
            if (request.getStudentLat() == null || request.getStudentLng() == null) {
                throw new BadRequestException("LOCATION_REQUIRED");
            }
            double distance = distanceMeters(session.getLatitude(), session.getLongitude(),
                    request.getStudentLat(), request.getStudentLng());
            if (distance > session.getRadiusMeters() + 20) { // 20m tolerance
                throw new BadRequestException("OUT_OF_GEOFENCE");
            }
        }

        User student = userRepository.findUserById(studentId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        Attendance attendance = attendanceRepository
                .findBySession_IdAndStudent_Id(session.getId(), student.getId())
                .orElseGet(() -> {
                    Attendance entity = new Attendance();
                    entity.setSession(session);
                    entity.setStudent(student);
                    return entity;
                });

        // Determine status by time
        Attendance.Status status = now.isAfter(session.getStartTime().plusMinutes(10))
                ? Attendance.Status.LATE
                : Attendance.Status.PRESENT;
        attendance.setStatus(status);
        attendance.setStudentLat(request.getStudentLat());
        attendance.setStudentLng(request.getStudentLng());
        attendance.setNote("SELF_CHECK");
        attendance.setCheckedAt(now);

        Attendance saved = attendanceRepository.save(attendance);
        CheckAttendanceResponse response = attendanceMapper.toResponse(saved);
        response.setMessage("Self check-in successfully");
        response.setStudentName(student.getFullName());
        return response;
    }

    @PreAuthorize("hasRole('STUDENT')")
    public HybridCheckInResponse checkInHybrid(UUID studentId, org.springframework.web.multipart.MultipartFile image,
            UUID sessionId, Double lat, Double lng) {
        ClassSession session = classSessionRepository.findClassSessionById(sessionId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));
        ensureSessionEditable(session);

        // 1. Validate Enrollment
        if (!enrollmentRepository.existsByCourse_IdAndStudent_Id(session.getCourse().getId(), studentId)) {
            throw new BadRequestException("NOT_ENROLLED");
        }

        // 2. Validate Time
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime early = session.getStartTime().minusMinutes(15);
        LocalDateTime late = session.getEndTime().plusMinutes(15);
        if (now.isBefore(early) || now.isAfter(late)) {
            throw new BadRequestException("SESSION_NOT_ACTIVE");
        }

        // 3. Validate Location (Mandatory)
        if (session.getRadiusMeters() > 0) {
            if (lat == null || lng == null) {
                throw new BadRequestException("LOCATION_REQUIRED");
            }
            double distance = distanceMeters(session.getLatitude(), session.getLongitude(), lat, lng);
            if (distance > session.getRadiusMeters() + 20) {
                throw new BadRequestException("OUT_OF_GEOFENCE");
            }
        }

        User student = userRepository.findUserById(studentId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        // 4. Face Recognition (Strict)
        boolean faceMatched = false;
        String checkInType = "FACE_AND_LOCATION";
        String message = "Check-in successful (Face + Location)";
        Float confidence = 0f;

        HybridCheckInResponse faceResult = faceRecognitionService.verifyFace(studentId, image);
        if (Boolean.TRUE.equals(faceResult.getIsMatch())) {
            faceMatched = true;
            confidence = faceResult.getConfidence();
        } else {
            log.warn("Face verification failed. Confidence: {} < Threshold", faceResult.getConfidence());
            throw new com.example.web_ai.exception.FaceVerificationFailedException(
                    "Face verification failed (Confidence: " + faceResult.getConfidence() + ")");
        }

        // 5. Save Attendance
        Attendance attendance = attendanceRepository
                .findBySession_IdAndStudent_Id(session.getId(), student.getId())
                .orElseGet(() -> {
                    Attendance entity = new Attendance();
                    entity.setSession(session);
                    entity.setStudent(student);
                    return entity;
                });

        Attendance.Status status = now.isAfter(session.getStartTime().plusMinutes(10))
                ? Attendance.Status.LATE
                : Attendance.Status.PRESENT;

        attendance.setStatus(status);
        attendance.setStudentLat(lat);
        attendance.setStudentLng(lng);
        attendance.setNote(checkInType);
        attendance.setCheckedAt(now);

        Attendance saved = attendanceRepository.save(attendance);

        return HybridCheckInResponse.builder()
                .success(true)
                .isMatch(faceMatched)
                .confidence(confidence)
                .status(status.name())
                .message(message)
                .attendanceId(saved.getId())
                .checkInType(checkInType)
                .build();
    }

    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public com.example.web_ai.dto.response.TeacherCheckInResponse teacherCheckInFace(UUID sessionId,
            org.springframework.web.multipart.MultipartFile image) {
        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));
        ensureSessionEditable(session);

        // 1. Get all students in the course
        List<Enrollment> enrollments = enrollmentRepository.findByCourse_IdWithStudent(session.getCourse().getId());
        List<User> students = enrollments.stream().map(Enrollment::getStudent).collect(Collectors.toList());

        // 2. Identify student from image
        User identifiedStudent;
        Double confidence;

        try {
            org.springframework.data.util.Pair<User, Double> identificationResult = faceRecognitionService
                    .identifyStudent(students, image);
            identifiedStudent = identificationResult.getFirst();
            confidence = identificationResult.getSecond();
        } catch (com.example.web_ai.exception.FaceVerificationFailedException
                | com.example.web_ai.exception.FaceRecognitionApiException e) {
            return com.example.web_ai.dto.response.TeacherCheckInResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }

        // 3. Mark attendance
        Attendance attendance = attendanceRepository
                .findBySession_IdAndStudent_Id(session.getId(), identifiedStudent.getId())
                .orElseGet(() -> {
                    Attendance entity = new Attendance();
                    entity.setSession(session);
                    entity.setStudent(identifiedStudent);
                    return entity;
                });

        LocalDateTime now = LocalDateTime.now();
        Attendance.Status status = now.isAfter(session.getStartTime().plusMinutes(10))
                ? Attendance.Status.LATE
                : Attendance.Status.PRESENT;

        attendance.setStatus(status);
        attendance.setNote("TEACHER_FACE_SCAN");
        attendance.setCheckedAt(now);

        attendanceRepository.save(attendance);

        return com.example.web_ai.dto.response.TeacherCheckInResponse.builder()
                .success(true)
                .studentId(identifiedStudent.getId())
                .studentName(identifiedStudent.getFullName())
                .confidence(confidence.floatValue())
                .status(status.name())
                .message("Identified and checked in successfully")
                .build();
    }

    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public SessionAttendanceDetailResponse getSessionAttendance(UUID sessionId) {
        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));

        List<Attendance> records = attendanceRepository.findAllBySession_IdOrderByCheckedAtAsc(sessionId);
        List<AttendanceRecordResponse> recordResponses = records.stream()
                .map(this::toRecordResponse)
                .collect(Collectors.toList());

        AttendanceStatsResponse stats = buildStats(session, records);

        return SessionAttendanceDetailResponse.builder()
                .session(classSessionMapper.toResponse(session))
                .records(recordResponses)
                .stats(stats)
                .build();
    }

    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public AttendanceRecordResponse updateAttendance(UUID attendanceId, AttendanceUpdateRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new NotFoundException("ATTENDANCE_NOT_FOUND"));

        ensureSessionEditable(attendance.getSession());

        if (request.getStatus() != null) {
            attendance.setStatus(request.getStatus());
        }
        if (request.getStudentLat() != null) {
            attendance.setStudentLat(request.getStudentLat());
        }
        if (request.getStudentLng() != null) {
            attendance.setStudentLng(request.getStudentLng());
        }
        if (request.getNote() != null) {
            attendance.setNote(request.getNote());
        }
        attendance.setCheckedAt(LocalDateTime.now());

        return toRecordResponse(attendanceRepository.save(attendance));
    }

    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public List<SessionRosterItemResponse> getSessionRoster(UUID sessionId) {
        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));

        // load all attendance records for the session once
        List<Attendance> records = attendanceRepository.findAllBySession_IdOrderByCheckedAtAsc(sessionId);
        Map<UUID, Attendance> attendanceByStudent = records.stream()
                .collect(Collectors.toMap(a -> a.getStudent().getId(), a -> a));

        // build roster from enrollments of the course
        List<SessionRosterItemResponse> roster = enrollmentRepository
                .findByCourse_IdWithStudent(session.getCourse().getId())
                .stream()
                .map(e -> {
                    Attendance a = attendanceByStudent.get(e.getStudent().getId());
                    boolean marked = a != null && a.getStatus() != null && a.getStatus() != Attendance.Status.ABSENT;
                    return SessionRosterItemResponse.builder()
                            .studentId(e.getStudent().getId())
                            .studentName(e.getStudent().getFullName())
                            .studentEmail(e.getStudent().getEmail())
                            .marked(marked)
                            .status(a != null && a.getStatus() != null ? a.getStatus().name() : null)
                            .checkedAt(a != null ? a.getCheckedAt() : null)
                            .studentLat(a != null ? a.getStudentLat() : null)
                            .studentLng(a != null ? a.getStudentLng() : null)
                            .note(a != null ? a.getNote() : null)
                            .build();
                })
                .sorted((r1, r2) -> {
                    String n1 = r1.getStudentName() != null ? r1.getStudentName() : "";
                    String n2 = r2.getStudentName() != null ? r2.getStudentName() : "";
                    return n1.compareToIgnoreCase(n2);
                })
                .collect(Collectors.toList());

        return roster;
    }

    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public SeedAttendanceResponse seedAttendance(UUID sessionId) {
        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));
        ensureSessionEditable(session);

        // Load existing attendance to avoid duplicates
        List<Attendance> existing = attendanceRepository.findAllBySession_IdOrderByCheckedAtAsc(sessionId);
        Map<UUID, Attendance> byStudent = existing.stream()
                .collect(Collectors.toMap(a -> a.getStudent().getId(), a -> a, (a1, a2) -> a1));

        // Enrollments of this course
        List<Enrollment> enrollments = enrollmentRepository.findByCourse_IdWithStudent(session.getCourse().getId());

        int created = 0;
        for (Enrollment e : enrollments) {
            UUID studentId = e.getStudent().getId();
            if (byStudent.containsKey(studentId)) {
                continue;
            }
            Attendance a = new Attendance();
            a.setSession(session);
            a.setStudent(e.getStudent());
            a.setStatus(Attendance.Status.ABSENT);
            // checkedAt will be set by @PrePersist; that's acceptable for seeded records
            attendanceRepository.save(a);
            created++;
        }

        int skipped = enrollments.size() - created;
        return SeedAttendanceResponse.builder()
                .sessionId(session.getId())
                .totalEnrollments(enrollments.size())
                .createdCount(created)
                .skippedCount(skipped)
                .build();
    }

    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public List<SessionAttendanceSummaryResponse> monitorSessions(UUID courseId,
            UUID teacherId,
            Integer minutesBefore,
            Integer minutesAfter) {
        int before = minutesBefore != null && minutesBefore > 0 ? minutesBefore : 15;
        int after = minutesAfter != null && minutesAfter > 0 ? minutesAfter : 30;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(before);
        LocalDateTime windowEnd = now.plusMinutes(after);

        List<SessionAttendanceSummaryProjection> projections = attendanceRepository.findLiveSessions(windowStart,
                windowEnd, courseId, teacherId);

        return projections.stream().map(this::toSummaryResponse).collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public List<SessionAttendanceSummaryResponse> reviewSessions(LocalDate from,
            LocalDate to,
            UUID courseId,
            UUID teacherId) {
        LocalDate startDate = from != null ? from : LocalDate.now();
        LocalDate endDate = to != null ? to : startDate;

        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("INVALID_DATE_RANGE");
        }

        LocalDateTime fromDateTime = startDate.atStartOfDay();
        LocalDateTime toDateTime = endDate.plusDays(1).atStartOfDay().minusSeconds(1);

        List<SessionAttendanceSummaryProjection> projections = attendanceRepository
                .findSessionsWithinRange(fromDateTime, toDateTime, courseId, teacherId);

        return projections.stream().map(this::toSummaryResponse).collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ClassSessionResponse toggleLock(UUID sessionId, boolean lock) {
        ClassSession session = classSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND"));
        session.setLocked(lock);
        return classSessionMapper.toResponse(classSessionRepository.save(session));
    }

    private AttendanceRecordResponse toRecordResponse(Attendance attendance) {
        return AttendanceRecordResponse.builder()
                .attendanceId(attendance.getId())
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getFullName())
                .status(attendance.getStatus())
                .checkedAt(attendance.getCheckedAt())
                .studentLat(attendance.getStudentLat())
                .studentLng(attendance.getStudentLng())
                .note(attendance.getNote())
                .build();
    }

    private AttendanceStatsResponse buildStats(ClassSession session, List<Attendance> records) {
        long totalEnrolled = enrollmentRepository.countByCourse_Id(session.getCourse().getId());
        long present = records.stream().filter(a -> a.getStatus() == Attendance.Status.PRESENT).count();
        long late = records.stream().filter(a -> a.getStatus() == Attendance.Status.LATE).count();
        long absent = records.stream().filter(a -> a.getStatus() == Attendance.Status.ABSENT).count();
        long excused = records.stream().filter(a -> a.getStatus() == Attendance.Status.EXCUSED).count();
        double attendanceRate = totalEnrolled > 0 ? ((double) (present + late) / totalEnrolled) * 100 : 0.0;

        return AttendanceStatsResponse.builder()
                .sessionId(session.getId())
                .courseName(session.getCourse().getName())
                .courseCode(session.getCourse().getCode())
                .roomName(session.getRoomName())
                .totalEnrolledStudents(totalEnrolled)
                .presentStudents(present)
                .lateStudents(late)
                .absentStudents(absent)
                .excusedStudents(excused)
                .attendanceRate(Math.round(attendanceRate * 100.0) / 100.0)
                .build();
    }

    private SessionAttendanceSummaryResponse toSummaryResponse(SessionAttendanceSummaryProjection projection) {
        return SessionAttendanceSummaryResponse.builder()
                .sessionId(projection.getSessionId())
                .courseId(projection.getCourseId())
                .courseName(projection.getCourseName())
                .courseCode(projection.getCourseCode())
                .teacherId(projection.getTeacherId())
                .teacherName(projection.getTeacherName())
                .roomName(projection.getRoomName())
                .startTime(projection.getStartTime())
                .endTime(projection.getEndTime())
                .locked(Boolean.TRUE.equals(projection.getLocked()))
                .totalEnrolled(safe(projection.getTotalEnrolled()))
                .totalMarked(safe(projection.getTotalMarked()))
                .presentCount(safe(projection.getPresentCount()))
                .lateCount(safe(projection.getLateCount()))
                .absentCount(safe(projection.getAbsentCount()))
                .excusedCount(safe(projection.getExcusedCount()))
                .build();
    }

    private long safe(Long value) {
        return value != null ? value : 0L;
    }

    private void ensureSessionEditable(ClassSession session) {
        if (session.isLocked()) {
            throw new BadRequestException("SESSION_LOCKED");
        }
    }

    private double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000.0; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
