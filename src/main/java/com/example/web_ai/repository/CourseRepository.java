package com.example.web_ai.repository;

import com.example.web_ai.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    boolean existsByCode(String code);
    List<Course> findAllByFaculty_id(UUID facultyId);
    List<Course> findAllByFaculty_code(String code);
    Page<Course> findAllByFaculty_code(String code, Pageable pageable);
    
    // Search by name with pagination - case insensitive, partial match
    Page<Course> findByNameContainingIgnoreCase(String name, Pageable pageable);

    long countByTeacher_Id(UUID teacherId);
}
