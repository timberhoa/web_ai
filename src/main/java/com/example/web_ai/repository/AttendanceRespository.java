package com.example.web_ai.repository;

import com.example.web_ai.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRespository extends JpaRepository<Attendance, Long> {

}
