// FacultyService.java
package com.example.web_ai.service;

import com.example.web_ai.dto.request.FacultyRequest;
import com.example.web_ai.dto.response.FacultyResponse;
import com.example.web_ai.entity.Faculty;
import com.example.web_ai.exception.BadRequestException;
import com.example.web_ai.exception.NotFoundException;
import com.example.web_ai.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN')")
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyResponse addFaculty(FacultyRequest req) {
        if (req.getCode() == null || req.getCode().trim().isEmpty())
            throw new BadRequestException("CODE_REQUIRED");
        if (req.getName() == null || req.getName().trim().isEmpty())
            throw new BadRequestException("NAME_REQUIRED");
        if (facultyRepository.existsByCode(req.getCode()))
            throw new BadRequestException("FACULTY_CODE_ALREADY_EXISTS");

        Faculty f = Faculty.builder()
                .code(req.getCode())
                .name(req.getName())
                .build();

        Faculty saved = facultyRepository.save(f);
        return FacultyResponse.fromEntity(saved);
    }

    public FacultyResponse updateFaculty(UUID id, FacultyRequest req) {
        Faculty f = facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("FACULTY_NOT_FOUND"));

        if (req.getCode() != null && !req.getCode().trim().isEmpty()) {
            if (facultyRepository.existsByCodeAndIdNot(req.getCode(), id))
                throw new BadRequestException("FACULTY_CODE_ALREADY_EXISTS");
            f.setCode(req.getCode());
        }
        if (req.getName() != null && !req.getName().trim().isEmpty()) {
            f.setName(req.getName());
        }

        Faculty saved = facultyRepository.save(f);
        return FacultyResponse.fromEntity(saved);
    }

    public void deleteFaculty(UUID id) {
        Faculty f = facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("FACULTY_NOT_FOUND"));
        facultyRepository.delete(f);
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT','TEACHER')")
    public FacultyResponse getFaculty(UUID id) {
        Faculty f = facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("FACULTY_NOT_FOUND"));
        return FacultyResponse.fromEntity(f);
    }

    public List<FacultyResponse> listFaculties() {
        return facultyRepository.findAll()
                .stream()
                .map(FacultyResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
