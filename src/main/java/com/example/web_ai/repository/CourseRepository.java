package com.example.web_ai.repository;

import com.example.web_ai.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    boolean existsByCode(String code);
    List<Course> findAllByFaculty_id(UUID facultyId);
    List<Course> findAllByFaculty_code(String code);
}
