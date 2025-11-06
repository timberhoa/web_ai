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

            Faculty engineeringFaculty = facultyRepository.findByCode("ENG01")
                    .orElseGet(() -> Faculty.builder()
                            .code("ENG01")
                            .name("Engineering")
                            .head(null)
                            .build());

            Faculty medicineFaculty = facultyRepository.findByCode("MED01")
                    .orElseGet(() -> Faculty.builder()
                            .code("MED01")
                            .name("Medicine")
                            .head(null)
                            .build());

            Faculty artsFaculty = facultyRepository.findByCode("ART01")
                    .orElseGet(() -> Faculty.builder()
                            .code("ART01")
                            .name("Arts and Literature")
                            .head(null)
                            .build());

            // Save faculties first (without heads)
            itFaculty = facultyRepository.save(itFaculty);
            businessFaculty = facultyRepository.save(businessFaculty);
            engineeringFaculty = facultyRepository.save(engineeringFaculty);
            medicineFaculty = facultyRepository.save(medicineFaculty);
            artsFaculty = facultyRepository.save(artsFaculty);

            System.out.println("✅ Faculties created");

            // ======== STEP 2: CREATE USERS WITH FACULTY REFERENCES ========

            // IT Faculty Students (5 students)
            User student1 = new User(null, "Nguyen Van A", "studentA", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "a@student.com", "0901111111", Role.STUDENT, true, itFaculty, new ArrayList<>());

            User student2 = new User(null, "Tran Thi B", "studentB", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "b@student.com", "0902222222", Role.STUDENT, true, itFaculty, new ArrayList<>());

            User student3 = new User(null, "Le Van C", "studentC", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "c@student.com", "0903333333", Role.STUDENT, true, itFaculty, new ArrayList<>());

            User student4 = new User(null, "Hoang Thi D", "studentD", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "d@student.com", "0904444444", Role.STUDENT, true, itFaculty, new ArrayList<>());

            User student5 = new User(null, "Pham Van E", "studentE", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "e@student.com", "0905555555", Role.STUDENT, true, itFaculty, new ArrayList<>());

            // Business Faculty Students (3 students)
            User student6 = new User(null, "Vo Thi F", "studentF", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "f@student.com", "0906666666", Role.STUDENT, true, businessFaculty, new ArrayList<>());

            User student7 = new User(null, "Dao Van G", "studentG", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "g@student.com", "0907777777", Role.STUDENT, true, businessFaculty, new ArrayList<>());

            User student8 = new User(null, "Bui Thi H", "studentH", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "h@student.com", "0908888888", Role.STUDENT, true, businessFaculty, new ArrayList<>());

            // Engineering Faculty Students (4 students)
            User student9 = new User(null, "Ngo Van I", "studentI", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "i@student.com", "0909999999", Role.STUDENT, true, engineeringFaculty, new ArrayList<>());

            User student10 = new User(null, "Ly Thi J", "studentJ", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "j@student.com", "0901000000", Role.STUDENT, true, engineeringFaculty, new ArrayList<>());

            User student11 = new User(null, "Do Van K", "studentK", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "k@student.com", "0901010101", Role.STUDENT, true, engineeringFaculty, new ArrayList<>());

            User student12 = new User(null, "Ha Thi L", "studentL", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "l@student.com", "0901212121", Role.STUDENT, true, engineeringFaculty, new ArrayList<>());

            // Medicine Faculty Students (2 students)
            User student13 = new User(null, "Cao Van M", "studentM", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "m@student.com", "0901313131", Role.STUDENT, true, medicineFaculty, new ArrayList<>());

            User student14 = new User(null, "Mai Thi N", "studentN", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "n@student.com", "0901414141", Role.STUDENT, true, medicineFaculty, new ArrayList<>());

            // Arts Faculty Students (1 student)
            User student15 = new User(null, "Trinh Van O", "studentO", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "o@student.com", "0901515151", Role.STUDENT, true, artsFaculty, new ArrayList<>());

            // Teachers
            User teacher1 = new User(null, "Dr. Pham IT", "teacherIT", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "it@teacher.com", "0901616161", Role.TEACHER, true, itFaculty, new ArrayList<>());

            User teacher2 = new User(null, "Dr. Nguyen BUS", "teacherBUS", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "bus@teacher.com", "0901717171", Role.TEACHER, true, businessFaculty, new ArrayList<>());

            User teacher3 = new User(null, "Dr. Le ENG", "teacherENG", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "eng@teacher.com", "0901818181", Role.TEACHER, true, engineeringFaculty, new ArrayList<>());

            User teacher4 = new User(null, "Dr. Tran MED", "teacherMED", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "med@teacher.com", "0901919191", Role.TEACHER, true, medicineFaculty, new ArrayList<>());

            User teacher5 = new User(null, "Dr. Vo ART", "teacherART", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "art@teacher.com", "0902020202", Role.TEACHER, true, artsFaculty, new ArrayList<>());

            User admin = new User(null, "Admin System", "admin", "$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq",
                    "admin@system.com", "0902121212", Role.ADMIN, true, null, new ArrayList<>());

            // Save all students
            student1 = userRepository.save(student1);
            student2 = userRepository.save(student2);
            student3 = userRepository.save(student3);
            student4 = userRepository.save(student4);
            student5 = userRepository.save(student5);
            student6 = userRepository.save(student6);
            student7 = userRepository.save(student7);
            student8 = userRepository.save(student8);
            student9 = userRepository.save(student9);
            student10 = userRepository.save(student10);
            student11 = userRepository.save(student11);
            student12 = userRepository.save(student12);
            student13 = userRepository.save(student13);
            student14 = userRepository.save(student14);
            student15 = userRepository.save(student15);

            // Save teachers
            teacher1 = userRepository.save(teacher1);
            teacher2 = userRepository.save(teacher2);
            teacher3 = userRepository.save(teacher3);
            teacher4 = userRepository.save(teacher4);
            teacher5 = userRepository.save(teacher5);
            admin = userRepository.save(admin);

            System.out.println("✅ Users created");

            // ======== STEP 3: UPDATE FACULTIES WITH HEADS ========
            itFaculty.setHead(teacher1);
            businessFaculty.setHead(teacher2);
            engineeringFaculty.setHead(teacher3);
            medicineFaculty.setHead(teacher4);
            artsFaculty.setHead(teacher5);

            // Save faculties again (now with heads)
            facultyRepository.save(itFaculty);
            facultyRepository.save(businessFaculty);
            facultyRepository.save(engineeringFaculty);
            facultyRepository.save(medicineFaculty);
            facultyRepository.save(artsFaculty);

            System.out.println("✅ Faculty heads assigned");

            // ======== STEP 4: CREATE COURSES ========
            Course course1 = new Course(null, "IT101", "Introduction to Programming", teacher1, 3, itFaculty);
            Course course2 = new Course(null, "IT201", "Database Systems", teacher1, 3, itFaculty);
            Course course3 = new Course(null, "BUS101", "Principles of Management", teacher2, 3, businessFaculty);
            Course course4 = new Course(null, "BUS201", "Marketing Basics", teacher2, 3, businessFaculty);
            Course course5 = new Course(null, "ENG101", "Engineering Mathematics", teacher3, 4, engineeringFaculty);
            Course course6 = new Course(null, "MED101", "Anatomy Basics", teacher4, 5, medicineFaculty);

            course1 = courseRepository.save(course1);
            course2 = courseRepository.save(course2);
            course3 = courseRepository.save(course3);
            course4 = courseRepository.save(course4);
            course5 = courseRepository.save(course5);
            course6 = courseRepository.save(course6);

            System.out.println("✅ Courses created");

            // ======== STEP 5: CREATE CLASS SESSIONS ========
            // IT101 Sessions
            ClassSession session1 = new ClassSession(null, course1,
                    LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2).plusHours(2),
                    "Room A101", 10.762622, 106.660172, 50);

            ClassSession session2 = new ClassSession(null, course1,
                    LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1).plusHours(2),
                    "Room A101", 10.762622, 106.660172, 50);

            // IT201 Session
            ClassSession session3 = new ClassSession(null, course2,
                    LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2),
                    "Room B202", 10.762000, 106.661000, 50);

            // BUS101 Session
            ClassSession session4 = new ClassSession(null, course3,
                    LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2),
                    "Room C303", 10.763000, 106.662000, 50);

            // ENG101 Session
            ClassSession session5 = new ClassSession(null, course5,
                    LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(3).plusHours(2),
                    "Room D404", 10.764000, 106.663000, 50);

            classSessionRepository.saveAll(List.of(session1, session2, session3, session4, session5));

            System.out.println("✅ Class sessions created");

            // ======== STEP 6: CREATE ENROLLMENTS ========
            // IT101 enrollments (5 students)
            Enrollment e1 = new Enrollment(null, course1, student1);
            Enrollment e2 = new Enrollment(null, course1, student2);
            Enrollment e3 = new Enrollment(null, course1, student3);
            Enrollment e4 = new Enrollment(null, course1, student4);
            Enrollment e5 = new Enrollment(null, course1, student5);

            // IT201 enrollments (3 students)
            Enrollment e6 = new Enrollment(null, course2, student1);
            Enrollment e7 = new Enrollment(null, course2, student2);
            Enrollment e8 = new Enrollment(null, course2, student3);

            // BUS101 enrollments (3 students)
            Enrollment e9 = new Enrollment(null, course3, student6);
            Enrollment e10 = new Enrollment(null, course3, student7);
            Enrollment e11 = new Enrollment(null, course3, student8);

            // ENG101 enrollments (4 students)
            Enrollment e12 = new Enrollment(null, course5, student9);
            Enrollment e13 = new Enrollment(null, course5, student10);
            Enrollment e14 = new Enrollment(null, course5, student11);
            Enrollment e15 = new Enrollment(null, course5, student12);

            // MED101 enrollments (2 students)
            Enrollment e16 = new Enrollment(null, course6, student13);
            Enrollment e17 = new Enrollment(null, course6, student14);

            enrollmentRepository.saveAll(List.of(
                    e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16, e17
            ));

            System.out.println("✅ Enrollments created");

            // ======== STEP 7: CREATE ATTENDANCES ========
            // Session 1 (IT101 - First session) - 5 enrolled, 4 attended
            Attendance a1 = new Attendance(null, session1, student1,
                    LocalDateTime.now().minusDays(2), 10.762620, 106.660150, Attendance.Status.PRESENT);
            Attendance a2 = new Attendance(null, session1, student2,
                    LocalDateTime.now().minusDays(2), 10.762800, 106.660200, Attendance.Status.LATE);
            Attendance a3 = new Attendance(null, session1, student3,
                    LocalDateTime.now().minusDays(2), 10.762700, 106.660180, Attendance.Status.PRESENT);
            Attendance a4 = new Attendance(null, session1, student4,
                    LocalDateTime.now().minusDays(2), null, null, Attendance.Status.ABSENT);

            // Session 2 (IT101 - Second session) - 5 enrolled, 3 attended
            Attendance a5 = new Attendance(null, session2, student1,
                    LocalDateTime.now().minusDays(1), 10.762620, 106.660150, Attendance.Status.PRESENT);
            Attendance a6 = new Attendance(null, session2, student2,
                    LocalDateTime.now().minusDays(1), null, null, Attendance.Status.ABSENT);
            Attendance a7 = new Attendance(null, session2, student5,
                    LocalDateTime.now().minusDays(1), 10.762650, 106.660160, Attendance.Status.EXCUSED);

            // Session 5 (ENG101) - 4 enrolled, 4 attended (perfect attendance)
            Attendance a8 = new Attendance(null, session5, student9,
                    LocalDateTime.now().minusDays(3), 10.764020, 106.663050, Attendance.Status.PRESENT);
            Attendance a9 = new Attendance(null, session5, student10,
                    LocalDateTime.now().minusDays(3), 10.764030, 106.663060, Attendance.Status.PRESENT);
            Attendance a10 = new Attendance(null, session5, student11,
                    LocalDateTime.now().minusDays(3), 10.764040, 106.663070, Attendance.Status.LATE);
            Attendance a11 = new Attendance(null, session5, student12,
                    LocalDateTime.now().minusDays(3), 10.764050, 106.663080, Attendance.Status.PRESENT);

            attendanceRepository.saveAll(List.of(
                    a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11
            ));

            System.out.println("✅ Attendances created");

            // ======== STEP 8: CREATE COURSE LECTURERS ========
            CourseLecturer cl1 = new CourseLecturer(null, course1, teacher1);
            CourseLecturer cl2 = new CourseLecturer(null, course2, teacher1);
            CourseLecturer cl3 = new CourseLecturer(null, course3, teacher2);
            CourseLecturer cl4 = new CourseLecturer(null, course4, teacher2);
            CourseLecturer cl5 = new CourseLecturer(null, course5, teacher3);
            CourseLecturer cl6 = new CourseLecturer(null, course6, teacher4);

            courseLecturerRepository.saveAll(List.of(cl1, cl2, cl3, cl4, cl5, cl6));

            System.out.println("✅ Course lecturers created");

            System.out.println("✅ Data initialization completed successfully!");
            System.out.println("📊 Summary:");
            System.out.println("   - 5 Faculties created");
            System.out.println("   - 15 Students created (IT: 5, Business: 3, Engineering: 4, Medicine: 2, Arts: 1)");
            System.out.println("   - 5 Teachers + 1 Admin created");
            System.out.println("   - 6 Courses created");
            System.out.println("   - 5 Class Sessions created");
            System.out.println("   - 17 Enrollments created");
            System.out.println("   - 11 Attendance records created");
            System.out.println("🎯 Ready to test faculty stats and attendance stats endpoints!");
            
            // In ra các sessionId để test
            System.out.println("\n📋 Available Session IDs for testing:");
            System.out.println("   Session 1 (IT101): " + session1.getId());
            System.out.println("   Session 2 (IT101): " + session2.getId());
            System.out.println("   Session 3 (IT201): " + session3.getId());
            System.out.println("   Session 4 (BUS101): " + session4.getId());
            System.out.println("   Session 5 (ENG101): " + session5.getId());
        };
    }
}