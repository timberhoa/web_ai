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
            AttendanceRepository attendanceRepository,
            CourseLecturerRepository courseLecturerRepository
    ) {
        return args -> {
            System.out.println("🚀 Starting data initialization...");

            // Check if users table is already seeded
            if (userRepository.count() > 0) {
                System.out.println("✅ Database already seeded. Skipping initialization.");
                return;
            }

            // ======== STEP 1: CREATE FACULTIES ========
            Faculty itFaculty = facultyRepository.findByCode("IT01")
                    .orElseGet(() -> Faculty.builder()
                            .code("IT01")
                            .name("Information Technology")
                            .build());

            Faculty businessFaculty = facultyRepository.findByCode("BUS01")
                    .orElseGet(() -> Faculty.builder()
                            .code("BUS01")
                            .name("Business Administration")
                            .build());

            Faculty engineeringFaculty = facultyRepository.findByCode("ENG01")
                    .orElseGet(() -> Faculty.builder()
                            .code("ENG01")
                            .name("Engineering")
                            .build());

            Faculty medicineFaculty = facultyRepository.findByCode("MED01")
                    .orElseGet(() -> Faculty.builder()
                            .code("MED01")
                            .name("Medicine")
                            .build());

            Faculty artsFaculty = facultyRepository.findByCode("ART01")
                    .orElseGet(() -> Faculty.builder()
                            .code("ART01")
                            .name("Arts and Literature")
                            .build());

            // Save faculties
            itFaculty = facultyRepository.save(itFaculty);
            businessFaculty = facultyRepository.save(businessFaculty);
            engineeringFaculty = facultyRepository.save(engineeringFaculty);
            medicineFaculty = facultyRepository.save(medicineFaculty);
            artsFaculty = facultyRepository.save(artsFaculty);

            System.out.println("✅ Faculties created");

            // ======== STEP 2: CREATE USERS WITH FACULTY REFERENCES ========

            // IT Faculty Students (5 students)
            User student1 = new User();
            student1.setFullName("Nguyen Van A");
            student1.setUsername("studentA");
            student1.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student1.setEmail("a@student.com");
            student1.setPhone("0901111111");
            student1.setRole(Role.STUDENT);
            student1.setActive(true);
            student1.setFaculty(itFaculty);

            User student2 = new User();
            student2.setFullName("Tran Thi B");
            student2.setUsername("studentB");
            student2.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student2.setEmail("b@student.com");
            student2.setPhone("0902222222");
            student2.setRole(Role.STUDENT);
            student2.setActive(true);
            student2.setFaculty(itFaculty);

            User student3 = new User();
            student3.setFullName("Le Van C");
            student3.setUsername("studentC");
            student3.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student3.setEmail("c@student.com");
            student3.setPhone("0903333333");
            student3.setRole(Role.STUDENT);
            student3.setActive(true);
            student3.setFaculty(itFaculty);

            User student4 = new User();
            student4.setFullName("Hoang Thi D");
            student4.setUsername("studentD");
            student4.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student4.setEmail("d@student.com");
            student4.setPhone("0904444444");
            student4.setRole(Role.STUDENT);
            student4.setActive(true);
            student4.setFaculty(itFaculty);

            User student5 = new User();
            student5.setFullName("Pham Van E");
            student5.setUsername("studentE");
            student5.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student5.setEmail("e@student.com");
            student5.setPhone("0905555555");
            student5.setRole(Role.STUDENT);
            student5.setActive(true);
            student5.setFaculty(itFaculty);

            // Business Faculty Students (3 students)
            User student6 = new User();
            student6.setFullName("Vo Thi F");
            student6.setUsername("studentF");
            student6.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student6.setEmail("f@student.com");
            student6.setPhone("0906666666");
            student6.setRole(Role.STUDENT);
            student6.setActive(true);
            student6.setFaculty(businessFaculty);

            User student7 = new User();
            student7.setFullName("Dao Van G");
            student7.setUsername("studentG");
            student7.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student7.setEmail("g@student.com");
            student7.setPhone("0907777777");
            student7.setRole(Role.STUDENT);
            student7.setActive(true);
            student7.setFaculty(businessFaculty);

            User student8 = new User();
            student8.setFullName("Bui Thi H");
            student8.setUsername("studentH");
            student8.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student8.setEmail("h@student.com");
            student8.setPhone("0908888888");
            student8.setRole(Role.STUDENT);
            student8.setActive(true);
            student8.setFaculty(businessFaculty);

            // Engineering Faculty Students (4 students)
            User student9 = new User();
            student9.setFullName("Ngo Van I");
            student9.setUsername("studentI");
            student9.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student9.setEmail("i@student.com");
            student9.setPhone("0909999999");
            student9.setRole(Role.STUDENT);
            student9.setActive(true);
            student9.setFaculty(engineeringFaculty);

            User student10 = new User();
            student10.setFullName("Ly Thi J");
            student10.setUsername("studentJ");
            student10.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student10.setEmail("j@student.com");
            student10.setPhone("0901000000");
            student10.setRole(Role.STUDENT);
            student10.setActive(true);
            student10.setFaculty(engineeringFaculty);

            User student11 = new User();
            student11.setFullName("Do Van K");
            student11.setUsername("studentK");
            student11.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student11.setEmail("k@student.com");
            student11.setPhone("0901010101");
            student11.setRole(Role.STUDENT);
            student11.setActive(true);
            student11.setFaculty(engineeringFaculty);

            User student12 = new User();
            student12.setFullName("Ha Thi L");
            student12.setUsername("studentL");
            student12.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student12.setEmail("l@student.com");
            student12.setPhone("0901212121");
            student12.setRole(Role.STUDENT);
            student12.setActive(true);
            student12.setFaculty(engineeringFaculty);

            // Medicine Faculty Students (2 students)
            User student13 = new User();
            student13.setFullName("Cao Van M");
            student13.setUsername("studentM");
            student13.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student13.setEmail("m@student.com");
            student13.setPhone("0901313131");
            student13.setRole(Role.STUDENT);
            student13.setActive(true);
            student13.setFaculty(medicineFaculty);

            User student14 = new User();
            student14.setFullName("Mai Thi N");
            student14.setUsername("studentN");
            student14.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student14.setEmail("n@student.com");
            student14.setPhone("0901414141");
            student14.setRole(Role.STUDENT);
            student14.setActive(true);
            student14.setFaculty(medicineFaculty);

            // Arts Faculty Students (1 student)
            User student15 = new User();
            student15.setFullName("Trinh Van O");
            student15.setUsername("studentO");
            student15.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            student15.setEmail("o@student.com");
            student15.setPhone("0901515151");
            student15.setRole(Role.STUDENT);
            student15.setActive(true);
            student15.setFaculty(artsFaculty);

            // Teachers
            User teacher1 = new User();
            teacher1.setFullName("Dr. Pham IT");
            teacher1.setUsername("teacherIT");
            teacher1.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            teacher1.setEmail("it@teacher.com");
            teacher1.setPhone("0901616161");
            teacher1.setRole(Role.TEACHER);
            teacher1.setActive(true);
            teacher1.setFaculty(itFaculty);

            User teacher2 = new User();
            teacher2.setFullName("Dr. Nguyen BUS");
            teacher2.setUsername("teacherBUS");
            teacher2.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            teacher2.setEmail("bus@teacher.com");
            teacher2.setPhone("0901717171");
            teacher2.setRole(Role.TEACHER);
            teacher2.setActive(true);
            teacher2.setFaculty(businessFaculty);

            User teacher3 = new User();
            teacher3.setFullName("Dr. Le ENG");
            teacher3.setUsername("teacherENG");
            teacher3.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            teacher3.setEmail("eng@teacher.com");
            teacher3.setPhone("0901818181");
            teacher3.setRole(Role.TEACHER);
            teacher3.setActive(true);
            teacher3.setFaculty(engineeringFaculty);

            User teacher4 = new User();
            teacher4.setFullName("Dr. Tran MED");
            teacher4.setUsername("teacherMED");
            teacher4.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            teacher4.setEmail("med@teacher.com");
            teacher4.setPhone("0901919191");
            teacher4.setRole(Role.TEACHER);
            teacher4.setActive(true);
            teacher4.setFaculty(medicineFaculty);

            User teacher5 = new User();
            teacher5.setFullName("Dr. Vo ART");
            teacher5.setUsername("teacherART");
            teacher5.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            teacher5.setEmail("art@teacher.com");
            teacher5.setPhone("0902020202");
            teacher5.setRole(Role.TEACHER);
            teacher5.setActive(true);
            teacher5.setFaculty(artsFaculty);

            User admin = new User();
            admin.setFullName("Admin System");
            admin.setUsername("admin");
            admin.setPassword("$2a$10$63ntGWUVmFWs0T6/mMwbdOm832a0P6KRcQw9JywzSuI33Ps5j4Xfq");
            admin.setEmail("admin@system.com");
            admin.setPhone("0902121212");
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            admin.setFaculty(null);

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

            // ======== STEP 3: CREATE COURSES ========
            Course course1 = new Course();
            course1.setCode("IT101");
            course1.setName("Introduction to Programming");
            course1.setTeacher(teacher1);
            course1.setCredits(3);
            course1.setFaculty(itFaculty);

            Course course2 = new Course();
            course2.setCode("IT201");
            course2.setName("Database Systems");
            course2.setTeacher(teacher1);
            course2.setCredits(3);
            course2.setFaculty(itFaculty);

            Course course3 = new Course();
            course3.setCode("BUS101");
            course3.setName("Principles of Management");
            course3.setTeacher(teacher2);
            course3.setCredits(3);
            course3.setFaculty(businessFaculty);

            Course course4 = new Course();
            course4.setCode("BUS201");
            course4.setName("Marketing Basics");
            course4.setTeacher(teacher2);
            course4.setCredits(3);
            course4.setFaculty(businessFaculty);

            Course course5 = new Course();
            course5.setCode("ENG101");
            course5.setName("Engineering Mathematics");
            course5.setTeacher(teacher3);
            course5.setCredits(4);
            course5.setFaculty(engineeringFaculty);

            Course course6 = new Course();
            course6.setCode("MED101");
            course6.setName("Anatomy Basics");
            course6.setTeacher(teacher4);
            course6.setCredits(5);
            course6.setFaculty(medicineFaculty);

            course1 = courseRepository.save(course1);
            course2 = courseRepository.save(course2);
            course3 = courseRepository.save(course3);
            course4 = courseRepository.save(course4);
            course5 = courseRepository.save(course5);
            course6 = courseRepository.save(course6);

            System.out.println("✅ Courses created");

            // ======== STEP 4: CREATE CLASS SESSIONS ========
            // IT101 Sessions
            ClassSession session1 = new ClassSession();
            session1.setCourse(course1);
            session1.setStartTime(LocalDateTime.now().minusDays(2));
            session1.setEndTime(LocalDateTime.now().minusDays(2).plusHours(2));
            session1.setRoomName("Room A101");
            session1.setLatitude(10.762622);
            session1.setLongitude(106.660172);
            session1.setRadiusMeters(50);

            ClassSession session2 = new ClassSession();
            session2.setCourse(course1);
            session2.setStartTime(LocalDateTime.now().minusDays(1));
            session2.setEndTime(LocalDateTime.now().minusDays(1).plusHours(2));
            session2.setRoomName("Room A101");
            session2.setLatitude(10.762622);
            session2.setLongitude(106.660172);
            session2.setRadiusMeters(50);

            // IT201 Session
            ClassSession session3 = new ClassSession();
            session3.setCourse(course2);
            session3.setStartTime(LocalDateTime.now().plusDays(1));
            session3.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
            session3.setRoomName("Room B202");
            session3.setLatitude(10.762000);
            session3.setLongitude(106.661000);
            session3.setRadiusMeters(50);

            // BUS101 Session
            ClassSession session4 = new ClassSession();
            session4.setCourse(course3);
            session4.setStartTime(LocalDateTime.now().plusDays(2));
            session4.setEndTime(LocalDateTime.now().plusDays(2).plusHours(2));
            session4.setRoomName("Room C303");
            session4.setLatitude(10.763000);
            session4.setLongitude(106.662000);
            session4.setRadiusMeters(50);

            // ENG101 Session
            ClassSession session5 = new ClassSession();
            session5.setCourse(course5);
            session5.setStartTime(LocalDateTime.now().minusDays(3));
            session5.setEndTime(LocalDateTime.now().minusDays(3).plusHours(2));
            session5.setRoomName("Room D404");
            session5.setLatitude(10.764000);
            session5.setLongitude(106.663000);
            session5.setRadiusMeters(50);

            classSessionRepository.saveAll(List.of(session1, session2, session3, session4, session5));

            System.out.println("✅ Class sessions created");

            // ======== STEP 5: CREATE ENROLLMENTS ========
            // IT101 enrollments (5 students)
            Enrollment e1 = new Enrollment();
            e1.setCourse(course1);
            e1.setStudent(student1);

            Enrollment e2 = new Enrollment();
            e2.setCourse(course1);
            e2.setStudent(student2);

            Enrollment e3 = new Enrollment();
            e3.setCourse(course1);
            e3.setStudent(student3);

            Enrollment e4 = new Enrollment();
            e4.setCourse(course1);
            e4.setStudent(student4);

            Enrollment e5 = new Enrollment();
            e5.setCourse(course1);
            e5.setStudent(student5);

            // IT201 enrollments (3 students)
            Enrollment e6 = new Enrollment();
            e6.setCourse(course2);
            e6.setStudent(student1);

            Enrollment e7 = new Enrollment();
            e7.setCourse(course2);
            e7.setStudent(student2);

            Enrollment e8 = new Enrollment();
            e8.setCourse(course2);
            e8.setStudent(student3);

            // BUS101 enrollments (3 students)
            Enrollment e9 = new Enrollment();
            e9.setCourse(course3);
            e9.setStudent(student6);

            Enrollment e10 = new Enrollment();
            e10.setCourse(course3);
            e10.setStudent(student7);

            Enrollment e11 = new Enrollment();
            e11.setCourse(course3);
            e11.setStudent(student8);

            // ENG101 enrollments (4 students)
            Enrollment e12 = new Enrollment();
            e12.setCourse(course5);
            e12.setStudent(student9);

            Enrollment e13 = new Enrollment();
            e13.setCourse(course5);
            e13.setStudent(student10);

            Enrollment e14 = new Enrollment();
            e14.setCourse(course5);
            e14.setStudent(student11);

            Enrollment e15 = new Enrollment();
            e15.setCourse(course5);
            e15.setStudent(student12);

            // MED101 enrollments (2 students)
            Enrollment e16 = new Enrollment();
            e16.setCourse(course6);
            e16.setStudent(student13);

            Enrollment e17 = new Enrollment();
            e17.setCourse(course6);
            e17.setStudent(student14);

            enrollmentRepository.saveAll(List.of(
                    e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16, e17
            ));

            System.out.println("✅ Enrollments created");

            // ======== STEP 6: CREATE ATTENDANCES ========
            // Session 1 (IT101 - First session) - 5 enrolled, 4 attended
            Attendance a1 = new Attendance();
            a1.setSession(session1);
            a1.setStudent(student1);
            a1.setCheckedAt(LocalDateTime.now().minusDays(2));
            a1.setStudentLat(10.762620);
            a1.setStudentLng(106.660150);
            a1.setStatus(Attendance.Status.PRESENT);

            Attendance a2 = new Attendance();
            a2.setSession(session1);
            a2.setStudent(student2);
            a2.setCheckedAt(LocalDateTime.now().minusDays(2));
            a2.setStudentLat(10.762800);
            a2.setStudentLng(106.660200);
            a2.setStatus(Attendance.Status.LATE);

            Attendance a3 = new Attendance();
            a3.setSession(session1);
            a3.setStudent(student3);
            a3.setCheckedAt(LocalDateTime.now().minusDays(2));
            a3.setStudentLat(10.762700);
            a3.setStudentLng(106.660180);
            a3.setStatus(Attendance.Status.PRESENT);

            Attendance a4 = new Attendance();
            a4.setSession(session1);
            a4.setStudent(student4);
            a4.setCheckedAt(LocalDateTime.now().minusDays(2));
            a4.setStudentLat(null);
            a4.setStudentLng(null);
            a4.setStatus(Attendance.Status.ABSENT);

            // Session 2 (IT101 - Second session) - 5 enrolled, 3 attended
            Attendance a5 = new Attendance();
            a5.setSession(session2);
            a5.setStudent(student1);
            a5.setCheckedAt(LocalDateTime.now().minusDays(1));
            a5.setStudentLat(10.762620);
            a5.setStudentLng(106.660150);
            a5.setStatus(Attendance.Status.PRESENT);

            Attendance a6 = new Attendance();
            a6.setSession(session2);
            a6.setStudent(student2);
            a6.setCheckedAt(LocalDateTime.now().minusDays(1));
            a6.setStudentLat(null);
            a6.setStudentLng(null);
            a6.setStatus(Attendance.Status.ABSENT);

            Attendance a7 = new Attendance();
            a7.setSession(session2);
            a7.setStudent(student5);
            a7.setCheckedAt(LocalDateTime.now().minusDays(1));
            a7.setStudentLat(10.762650);
            a7.setStudentLng(106.660160);
            a7.setStatus(Attendance.Status.EXCUSED);

            // Session 5 (ENG101) - 4 enrolled, 4 attended (perfect attendance)
            Attendance a8 = new Attendance();
            a8.setSession(session5);
            a8.setStudent(student9);
            a8.setCheckedAt(LocalDateTime.now().minusDays(3));
            a8.setStudentLat(10.764020);
            a8.setStudentLng(106.663050);
            a8.setStatus(Attendance.Status.PRESENT);

            Attendance a9 = new Attendance();
            a9.setSession(session5);
            a9.setStudent(student10);
            a9.setCheckedAt(LocalDateTime.now().minusDays(3));
            a9.setStudentLat(10.764030);
            a9.setStudentLng(106.663060);
            a9.setStatus(Attendance.Status.PRESENT);

            Attendance a10 = new Attendance();
            a10.setSession(session5);
            a10.setStudent(student11);
            a10.setCheckedAt(LocalDateTime.now().minusDays(3));
            a10.setStudentLat(10.764040);
            a10.setStudentLng(106.663070);
            a10.setStatus(Attendance.Status.LATE);

            Attendance a11 = new Attendance();
            a11.setSession(session5);
            a11.setStudent(student12);
            a11.setCheckedAt(LocalDateTime.now().minusDays(3));
            a11.setStudentLat(10.764050);
            a11.setStudentLng(106.663080);
            a11.setStatus(Attendance.Status.PRESENT);

            attendanceRepository.saveAll(List.of(
                    a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11
            ));

            System.out.println("✅ Attendances created");

            // ======== STEP 7: CREATE COURSE LECTURERS ========
            CourseLecturer cl1 = new CourseLecturer();
            cl1.setCourse(course1);
            cl1.setLecturer(teacher1);

            CourseLecturer cl2 = new CourseLecturer();
            cl2.setCourse(course2);
            cl2.setLecturer(teacher1);

            CourseLecturer cl3 = new CourseLecturer();
            cl3.setCourse(course3);
            cl3.setLecturer(teacher2);

            CourseLecturer cl4 = new CourseLecturer();
            cl4.setCourse(course4);
            cl4.setLecturer(teacher2);

            CourseLecturer cl5 = new CourseLecturer();
            cl5.setCourse(course5);
            cl5.setLecturer(teacher3);

            CourseLecturer cl6 = new CourseLecturer();
            cl6.setCourse(course6);
            cl6.setLecturer(teacher4);

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