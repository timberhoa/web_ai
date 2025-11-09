package com.example.web_ai.service;

import com.example.web_ai.dto.response.ClassSessionResponse;
import com.example.web_ai.dto.response.StudentAttendanceHistoryResponse;
import com.example.web_ai.dto.response.dashboard.AdminDashboardResponse;
import com.example.web_ai.dto.response.dashboard.StudentDashboardResponse;
import com.example.web_ai.dto.response.dashboard.TeacherDashboardResponse;
import com.example.web_ai.entity.Attendance;
import com.example.web_ai.entity.User;
import com.example.web_ai.enums.Role;
import com.example.web_ai.exception.NotFoundException;
import com.example.web_ai.mapper.ClassSessionMapper;
import com.example.web_ai.repository.AttendanceRepository;
import com.example.web_ai.repository.ClassSessionRepository;
import com.example.web_ai.repository.CourseRepository;
import com.example.web_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final List<Attendance.Status> PRESENT_STATUSES =
            Arrays.asList(Attendance.Status.PRESENT, Attendance.Status.LATE, Attendance.Status.EXCUSED);

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ClassSessionRepository classSessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final ClassSessionMapper classSessionMapper;
    private final StudentService studentService;

    public AdminDashboardResponse getAdminDashboard() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        long totalStudents = userRepository.countByRole(Role.STUDENT);
        long totalCourses = courseRepository.count();
        long sessionsToday = classSessionRepository.countByStartTimeBetween(startOfDay, endOfDay);
        long checkinsToday = attendanceRepository.countByCheckedAtBetween(startOfDay, endOfDay);

        long totalAttendanceRecords = attendanceRepository.count();
        long attendedRecords = attendanceRepository.countByStatusIn(PRESENT_STATUSES);
        double attendanceRate = totalAttendanceRecords == 0 ? 0.0
                : Math.round(((double) attendedRecords / totalAttendanceRecords) * 10000.0) / 100.0;

        return AdminDashboardResponse.builder()
                .totalStudents(totalStudents)
                .totalCourses(totalCourses)
                .sessionsToday(sessionsToday)
                .checkinsToday(checkinsToday)
                .attendanceRate(attendanceRate)
                .build();
    }

    public TeacherDashboardResponse getTeacherDashboard(UUID teacherId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        long totalCourses = courseRepository.countByTeacher_Id(teacherId);
        long sessionsToday = classSessionRepository
                .countByCourse_Teacher_IdAndStartTimeBetween(teacherId, startOfDay, endOfDay);
        long totalCheckinsToday = attendanceRepository
                .countBySession_Course_Teacher_IdAndCheckedAtBetween(teacherId, startOfDay, endOfDay);
        long absentToday = attendanceRepository
                .countBySession_Course_Teacher_IdAndCheckedAtBetweenAndStatus(
                        teacherId, startOfDay, endOfDay, Attendance.Status.ABSENT);
        long lateToday = attendanceRepository
                .countBySession_Course_Teacher_IdAndCheckedAtBetweenAndStatus(
                        teacherId, startOfDay, endOfDay, Attendance.Status.LATE);

        List<ClassSessionResponse> upcomingSessions = classSessionRepository
                .findTop5ByCourse_Teacher_IdAndStartTimeGreaterThanEqualOrderByStartTimeAsc(teacherId, startOfDay)
                .stream()
                .map(classSessionMapper::toResponse)
                .toList();

        return TeacherDashboardResponse.builder()
                .totalCourses(totalCourses)
                .sessionsToday(sessionsToday)
                .totalCheckinsToday(totalCheckinsToday)
                .absentToday(absentToday)
                .lateToday(lateToday)
                .upcomingSessions(upcomingSessions)
                .build();
    }

    public StudentDashboardResponse getStudentDashboard(UUID studentId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<ClassSessionResponse> todaySessions = studentService
                .getSessionsForRange(studentId, startOfDay, endOfDay, 10);

        long todayCheckins = attendanceRepository.countByStudent_IdAndCheckedAtBetween(studentId, startOfDay, endOfDay);
        StudentAttendanceHistoryResponse latestAttendance = attendanceRepository
                .findTopByStudent_IdOrderByCheckedAtDesc(studentId)
                .map(studentService::toHistoryResponse)
                .orElse(null);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        boolean faceRegistered = student.getImages() != null && !student.getImages().isEmpty();

        int totalCourses = studentService.getMyCourses(studentId).size();

        return StudentDashboardResponse.builder()
                .totalCourses(totalCourses)
                .todayCheckins(todayCheckins)
                .faceRegistered(faceRegistered)
                .todaySessions(todaySessions)
                .latestAttendance(latestAttendance)
                .build();
    }
}

