-- ============================================
-- Tạo database
-- ============================================
CREATE DATABASE IF NOT EXISTS db_1 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_1;

-- ============================================
-- Bảng FACULTY (tạo trước)
-- ============================================
CREATE TABLE faculty (
    id CHAR(36) PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    head_id CHAR(36)
) ENGINE=InnoDB;

-- ============================================
-- Bảng USER (tham chiếu faculty)
-- ============================================
CREATE TABLE user (
    id CHAR(36) PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    role ENUM('ADMIN', 'LECTURER', 'STUDENT') NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    faculty_id CHAR(36),
    CONSTRAINT fk_user_faculty FOREIGN KEY (faculty_id) REFERENCES faculty(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ✅ Sau khi cả hai bảng tồn tại, thêm FK faculty → user
ALTER TABLE faculty
ADD CONSTRAINT fk_faculty_head FOREIGN KEY (head_id)
REFERENCES user(id)
ON DELETE SET NULL
ON UPDATE CASCADE;

-- ============================================
-- Các bảng khác giữ nguyên
-- ============================================
CREATE TABLE course (
    id CHAR(36) PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    credit INT NOT NULL,
    faculty_id CHAR(36),
    CONSTRAINT fk_course_faculty FOREIGN KEY (faculty_id)
        REFERENCES faculty(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE course_lecture (
    id CHAR(36) PRIMARY KEY,
    course_id CHAR(36) NOT NULL,
    lecturer_id CHAR(36) NOT NULL,
    CONSTRAINT fk_courselecture_course FOREIGN KEY (course_id)
        REFERENCES course(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_courselecture_lecturer FOREIGN KEY (lecturer_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    UNIQUE(course_id, lecturer_id)
) ENGINE=InnoDB;

CREATE TABLE enrollment (
    id CHAR(36) PRIMARY KEY,
    course_id CHAR(36) NOT NULL,
    student_id CHAR(36) NOT NULL,
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id)
        REFERENCES course(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    UNIQUE(course_id, student_id)
) ENGINE=InnoDB;

CREATE TABLE class_session (
    id CHAR(36) PRIMARY KEY,
    course_id CHAR(36) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    room_name VARCHAR(100),
    latitude DOUBLE,
    longitude DOUBLE,
    radius_meter DOUBLE,
    CONSTRAINT fk_classsession_course FOREIGN KEY (course_id)
        REFERENCES course(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE attendance (
    id CHAR(36) PRIMARY KEY,
    session_id CHAR(36) NOT NULL,
    student_id CHAR(36) NOT NULL,
    checked_at DATETIME,
    student_lat DOUBLE,
    student_lng DOUBLE,
    status ENUM('PRESENT', 'ABSENT', 'EXCUSED') NOT NULL,
    CONSTRAINT fk_attendance_session FOREIGN KEY (session_id)
        REFERENCES class_session(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;
