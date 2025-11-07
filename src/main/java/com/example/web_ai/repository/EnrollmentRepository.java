package com.example.web_ai.repository;

import com.example.web_ai.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    
    // Use JOIN FETCH to avoid N+1 query problem
    @Query("SELECT e FROM Enrollment e " +
           "JOIN FETCH e.course c " +
           "LEFT JOIN FETCH c.teacher " +
           "LEFT JOIN FETCH c.faculty " +
           "WHERE e.student.id = :studentId")
    List<Enrollment> findByStudent_IdWithCourseDetails(@Param("studentId") UUID studentId);
    
    // Keep the original method for backward compatibility
    List<Enrollment> findByStudent_Id(UUID id);
}
