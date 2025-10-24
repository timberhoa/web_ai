-- ============================================
-- 3️⃣ Fake data
-- ============================================

-- Faculties
INSERT INTO faculty (id, code, name) VALUES
('f001', 'CS', 'Computer Science'),
('f002', 'EE', 'Electrical Engineering');

-- Users
INSERT INTO user (id, full_name, username, password, email, phone, role, active, faculty_id) VALUES
('u001', 'Dr. Nguyen Van A', 'head_cs', '123456', 'a@uni.edu', '0900000001', 'LECTURER', TRUE, 'f001'),
('u002', 'Mr. Tran Van B', 'lecturer_b', '123456', 'b@uni.edu', '0900000002', 'LECTURER', TRUE, 'f001'),
('u003', 'Nguyen Van C', 'student_c', '123456', 'c@uni.edu', '0900000003', 'STUDENT', TRUE, 'f001'),
('u004', 'Le Thi D', 'student_d', '123456', 'd@uni.edu', '0900000004', 'STUDENT', TRUE, 'f001'),
('u005', 'Pham Van E', 'student_e', '123456', 'e@uni.edu', '0900000005', 'STUDENT', TRUE, 'f001'),
('u006', 'Do Thi F', 'student_f', '123456', 'f@uni.edu', '0900000006', 'STUDENT', TRUE, 'f002'),
('u007', 'Hoang Van G', 'student_g', '123456', 'g@uni.edu', '0900000007', 'STUDENT', TRUE, 'f002'),
('u008', 'Tran Thi H', 'student_h', '123456', 'h@uni.edu', '0900000008', 'STUDENT', TRUE, 'f001'),
('u009', 'Bui Van I', 'student_i', '123456', 'i@uni.edu', '0900000009', 'STUDENT', TRUE, 'f001'),
('u010', 'Doan Van J', 'student_j', '123456', 'j@uni.edu', '0900000010', 'STUDENT', TRUE, 'f001');

-- Set faculty head
UPDATE faculty SET head_id = 'u001' WHERE id = 'f001';

-- Courses
INSERT INTO course (id, code, name, credit, faculty_id) VALUES
('c001', 'CS101', 'Introduction to Programming', 3, 'f001'),
('c002', 'CS201', 'Database Systems', 3, 'f001'),
('c003', 'EE101', 'Basic Electronics', 3, 'f002'),
('c004', 'CS301', 'AI Fundamentals', 4, 'f001');

-- Course_Lecture mapping
INSERT INTO course_lecture (id, course_id, lecturer_id) VALUES
('cl001', 'c001', 'u001'),
('cl002', 'c002', 'u002'),
('cl003', 'c004', 'u001'),
('cl004', 'c003', 'u002');

-- Enrollment (students in courses)
INSERT INTO enrollment (id, course_id, student_id) VALUES
('en001', 'c001', 'u003'),
('en002', 'c001', 'u004'),
('en003', 'c002', 'u005'),
('en004', 'c002', 'u008'),
('en005', 'c004', 'u009'),
('en006', 'c004', 'u010'),
('en007', 'c003', 'u006'),
('en008', 'c003', 'u007');

-- Class sessions
INSERT INTO class_session (id, course_id, start_time, end_time, room_name, latitude, longitude, radius_meter) VALUES
('s001', 'c001', '2025-10-01 08:00:00', '2025-10-01 09:30:00', 'A101', 10.762622, 106.660172, 50),
('s002', 'c002', '2025-10-02 09:00:00', '2025-10-02 10:30:00', 'A102', 10.762700, 106.660200, 50),
('s003', 'c003', '2025-10-03 08:00:00', '2025-10-03 09:30:00', 'E101', 10.762800, 106.660300, 60),
('s004', 'c004', '2025-10-04 10:00:00', '2025-10-04 11:30:00', 'A201', 10.762900, 106.660400, 40),
('s005', 'c004', '2025-10-05 13:00:00', '2025-10-05 14:30:00', 'A202', 10.763000, 106.660500, 40);

-- Attendance
INSERT INTO attendance (id, session_id, student_id, checked_at, student_lat, student_lng, status) VALUES
('a001', 's001', 'u003', '2025-10-01 08:05:00', 10.762600, 106.660160, 'PRESENT'),
('a002', 's001', 'u004', '2025-10-01 08:10:00', 10.762610, 106.660165, 'PRESENT'),
('a003', 's002', 'u005', NULL, NULL, NULL, 'ABSENT'),
('a004', 's002', 'u008', '2025-10-02 09:03:00', 10.762700, 106.660200, 'PRESENT'),
('a005', 's004', 'u009', '2025-10-04 10:01:00', 10.762900, 106.660400, 'PRESENT'),
('a006', 's004', 'u010', NULL, NULL, NULL, 'EXCUSED');