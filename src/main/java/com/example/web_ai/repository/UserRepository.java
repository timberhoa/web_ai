package com.example.web_ai.repository;

import com.example.web_ai.entity.User;
import com.example.web_ai.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
       Optional<User> findUserById(UUID id);

       Optional<User> findUserByUsername(String username);

       @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
       Optional<User> findByEmail(@Param("email") String email);

       List<User> findByRole(Role role);

       Page<User> findByRole(Role role, Pageable pageable);

       boolean existsByUsername(String username);

       boolean existsByEmail(String email);

       boolean existsByPhone(String phone);

       long countByRole(Role role);

       @Query("SELECT f.id, f.code, f.name, COUNT(u) " +
                     "FROM Faculty f LEFT JOIN User u ON f.id = u.faculty.id AND u.role = 'STUDENT' " +
                     "GROUP BY f.id, f.code, f.name")
       List<Object[]> findStudentCountByFaculty();

       // Search users by multiple fields (name, username, email, phone) - case
       // insensitive
       @Query("SELECT u FROM User u WHERE " +
                     "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(u.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
       Page<User> searchByMultipleFields(@Param("searchTerm") String searchTerm, Pageable pageable);

       // Search users by multiple fields with role filter
       @Query("SELECT u FROM User u WHERE " +
                     "u.role = :role AND (" +
                     "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(u.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
       Page<User> searchByMultipleFieldsAndRole(@Param("searchTerm") String searchTerm,
                     @Param("role") Role role,
                     Pageable pageable);
}
