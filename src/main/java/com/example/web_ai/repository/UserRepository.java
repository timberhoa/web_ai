package com.example.web_ai.repository;

import com.example.web_ai.entity.User;
import com.example.web_ai.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserById(UUID id);
    Optional<User> findUserByUsername(String username);
    List<User> findByRole(Role role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    
    @Query("SELECT f.id, f.code, f.name, COUNT(u) " +
           "FROM Faculty f LEFT JOIN User u ON f.id = u.faculty.id AND u.role = 'STUDENT' " +
           "GROUP BY f.id, f.code, f.name")
    List<Object[]> findStudentCountByFaculty();
}
