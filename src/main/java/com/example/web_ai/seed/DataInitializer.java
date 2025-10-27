package com.example.web_ai.seed;

import com.example.web_ai.entity.*;
import com.example.web_ai.enums.Role;
import com.example.web_ai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            FacultyRepository facultyRepository,
            CourseRepository courseRepository,
            ClassSessionRepository classSessionRepository,
            EnrollmentRepository enrollmentRepository,
            AttendanceRespository attendanceRepository,
            CourseLecturerRepository courseLecturerRepository
    ) {
        return args -> {
            System.out.println("🚀 Starting data initialization...");

            // Check if users table is already seeded
            if (userRepository.count() > 0) {
                System.out.println("✅ Database already seeded. Skipping initialization.");
                return;
            }

            // ======== STEP 1: CREATE FACULTIES WITHOUT HEADS ========
            Faculty itFaculty = facultyRepository.findByCode("IT01")
                    .orElseGet(() -> Faculty.builder()
                            .code("IT01")
                            .name("Information Technology")
                            .head(null)  // Explicitly set null
                            .build());

            Faculty businessFaculty = facultyRepository.findByCode("BUS01")
                    .orElseGet(() -> Faculty.builder()
                            .code("BUS01")
                            .name("Business Administration")
                            .head(null)  // Explicitly set null
                            .build());

            // Save faculties first (without heads)
            itFaculty = facultyRepository.save(itFaculty);
            businessFaculty = facultyRepository.save(businessFaculty);

            System.out.println("✅ Faculties created");

            // ======== STEP 2: CREATE USERS WITH FACULTY REFERENCES ========

            User student1 = new User(null, "Nguyen Van A", "studentA", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "a@student.com", "0901111111", Role.STUDENT, true, itFaculty, new ArrayList<>());

            User student2 = new User(null, "Tran Thi B", "studentB", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "b@student.com", "0902222222", Role.STUDENT, true, itFaculty, new ArrayList<>());

            User student3 = new User(null, "Le Van C", "studentC", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "c@student.com", "0903333333", Role.STUDENT, true, businessFaculty, new ArrayList<>());

            User teacher1 = new User(null, "Dr. Pham D", "teacherD", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "d@teacher.com", "0904444444", Role.TEACHER, true, itFaculty, new ArrayList<>());

            User teacher2 = new User(null, "Dr. Nguyen E", "teacherE", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "e@teacher.com", "0905555555", Role.TEACHER, true, businessFaculty, new ArrayList<>());

            User admin = new User(null, "Admin System", "admin", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "admin@system.com", "0906666666", Role.ADMIN, true, null, new ArrayList<>());

            // Save users
            student1 = userRepository.save(student1);
            student2 = userRepository.save(student2);
            student3 = userRepository.save(student3);
            teacher1 = userRepository.save(teacher1);
            teacher2 = userRepository.save(teacher2);
            admin = userRepository.save(admin);

            System.out.println("✅ Users created");

            // ======== STEP 3: UPDATE FACULTIES WITH HEADS ========
            itFaculty.setHead(teacher1);
            businessFaculty.setHead(teacher2);

            // Save faculties again (now with heads)
            facultyRepository.save(itFaculty);
            facultyRepository.save(businessFaculty);

            System.out.println("✅ Faculty heads assigned");

            // ======== STEP 4: CREATE COURSES ========
            Course course1 = new Course(null, "IT101", "Introduction to Programming", teacher1, 3, itFaculty);
            Course course2 = new Course(null, "IT201", "Database Systems", teacher1, 3, itFaculty);
            Course course3 = new Course(null, "BUS101", "Principles of Management", teacher2, 3, businessFaculty);
            Course course4 = new Course(null, "BUS201", "Marketing Basics", teacher2, 3, businessFaculty);

            course1 = courseRepository.save(course1);
            course2 = courseRepository.save(course2);
            course3 = courseRepository.save(course3);
            course4 = courseRepository.save(course4);

            System.out.println("✅ Courses created");

            // ======== STEP 5: CREATE CLASS SESSIONS ========
            ClassSession session1 = new ClassSession(null, course1,
                    LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1).plusHours(2),
                    "Room 101", 10.762622, 106.660172, 50);

            ClassSession session2 = new ClassSession(null, course2,
                    LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2),
                    "Room 202", 10.762000, 106.661000, 50);

            ClassSession session3 = new ClassSession(null, course3,
                    LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2),
                    "Room 303", 10.763000, 106.662000, 50);

            classSessionRepository.saveAll(List.of(session1, session2, session3));

            System.out.println("✅ Class sessions created");

            // ======== STEP 6: CREATE ENROLLMENTS ========
            Enrollment e1 = new Enrollment(null, course1, student1);
            Enrollment e2 = new Enrollment(null, course1, student2);
            Enrollment e3 = new Enrollment(null, course2, student1);
            Enrollment e4 = new Enrollment(null, course3, student3);

            enrollmentRepository.saveAll(List.of(e1, e2, e3, e4));

            System.out.println("✅ Enrollments created");

            // ======== STEP 7: CREATE ATTENDANCES ========
            Attendance a1 = new Attendance(null, session1, student1,
                    LocalDateTime.now().minusDays(1), 10.762620, 106.660150, Attendance.Status.PRESENT);

            Attendance a2 = new Attendance(null, session1, student2,
                    LocalDateTime.now().minusDays(1), 10.762800, 106.660200, Attendance.Status.LATE);

            Attendance a3 = new Attendance(null, session3, student3,
                    LocalDateTime.now().plusDays(2), 10.763100, 106.662200, Attendance.Status.ABSENT);

            attendanceRepository.saveAll(List.of(a1, a2, a3));

            System.out.println("✅ Attendances created");

            // ======== STEP 8: CREATE COURSE LECTURERS ========
            CourseLecturer cl1 = new CourseLecturer(null, course1, teacher1);
            CourseLecturer cl2 = new CourseLecturer(null, course2, teacher1);
            CourseLecturer cl3 = new CourseLecturer(null, course3, teacher2);

            courseLecturerRepository.saveAll(List.of(cl1, cl2, cl3));

            System.out.println("✅ Course lecturers created");

            System.out.println("✅ Data initialization completed successfully!");
        };
    }
}