package com.example.web_ai.repository;

import com.example.web_ai.entity.CourseLecturer;
import com.example.web_ai.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseLecturerRepository extends JpaRepository<CourseLecturer, UUID> {

}
