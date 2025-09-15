// FacultyService.java
package com.example.web_ai.service;

import com.example.web_ai.dto.request.FacultyRequest;
import com.example.web_ai.dto.response.FacultyResponse;
import com.example.web_ai.entity.Faculty;
import com.example.web_ai.entity.User;
import com.example.web_ai.enums.Role;
import com.example.web_ai.repository.FacultyRepository;
import com.example.web_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;

    public FacultyResponse addFaculty(FacultyRequest req) {
        if (req.getCode() == null || req.getCode().isBlank())
            throw new IllegalArgumentException("CODE_REQUIRED");
        if (req.getName() == null || req.getName().isBlank())
            throw new IllegalArgumentException("NAME_REQUIRED");
        if (facultyRepository.existsByCode(req.getCode()))
            throw new RuntimeException("FACULTY_CODE_ALREADY_EXISTS");

        User head = null;
        if (req.getTeacherId() != null) {
            head = userRepository.findById(req.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("TEACHER_NOT_FOUND"));
            if (!head.isActive()) throw new RuntimeException("TEACHER_INACTIVE");
            if (head.getRole() == Role.STUDENT) throw new RuntimeException("TEACHER_ROLE_INVALID");
        }

        Faculty f = Faculty.builder()
                .code(req.getCode())
                .name(req.getName())
                .teacher(head)
                .build();

        Faculty saved = facultyRepository.save(f);
        return FacultyResponse.fromEntity(saved);
    }

    public FacultyResponse updateFaculty(UUID id, FacultyRequest req) {
        Faculty f = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FACULTY_NOT_FOUND"));

        if (req.getCode() != null && !req.getCode().isBlank()) {
            if (facultyRepository.existsByCodeAndIdNot(req.getCode(), id))
                throw new RuntimeException("FACULTY_CODE_ALREADY_EXISTS");
            f.setCode(req.getCode());
        }
        if (req.getName() != null && !req.getName().isBlank()) {
            f.setName(req.getName());
        }
        if (req.getTeacherId() != null) {
            User head = userRepository.findById(req.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("TEACHER_NOT_FOUND"));
            if (!head.isActive()) throw new RuntimeException("TEACHER_INACTIVE");
            if (head.getRole() == Role.STUDENT) throw new RuntimeException("TEACHER_ROLE_INVALID");
            f.setTeacher(head);
        }

        Faculty saved = facultyRepository.save(f);
        return FacultyResponse.fromEntity(saved);
    }

    public void deleteFaculty(UUID id) {
        Faculty f = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FACULTY_NOT_FOUND"));
        facultyRepository.delete(f);
    }

    public FacultyResponse getFaculty(UUID id) {
        Faculty f = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FACULTY_NOT_FOUND"));
        return FacultyResponse.fromEntity(f);
    }

    public List<FacultyResponse> listFaculties() {
        return facultyRepository.findAll()
                .stream()
                .map(FacultyResponse::fromEntity)
                .toList();
    }
}
