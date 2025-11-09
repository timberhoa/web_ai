package com.example.web_ai.controller;

import com.example.web_ai.dto.request.FacultyRequest;
import com.example.web_ai.dto.response.FacultyResponse;
import com.example.web_ai.service.FacultyService;
import jakarta.annotation.security.PermitAll;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/faculties")
@RequiredArgsConstructor
@Tag(name = "Faculty", description = "Create and manage faculties")
public class FacultyController {

    private final FacultyService facultyService;

    @PostMapping
    @Operation(summary = "Create faculty", description = "Add a new faculty.")
    public ResponseEntity<FacultyResponse> addFaculty(@RequestBody FacultyRequest req) {
        return ResponseEntity.ok(facultyService.addFaculty(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update faculty", description = "Edit faculty name/description.")
    public ResponseEntity<FacultyResponse> updateFaculty(@PathVariable UUID id,
                                                         @RequestBody FacultyRequest req) {
        return ResponseEntity.ok(facultyService.updateFaculty(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete faculty", description = "Remove a faculty by ID.")
    public ResponseEntity<Void> deleteFaculty(@PathVariable UUID id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get faculty", description = "Get single faculty detail.")
    public ResponseEntity<FacultyResponse> getFaculty(@PathVariable UUID id) {
        return ResponseEntity.ok(facultyService.getFaculty(id));
    }
    @GetMapping
    @Operation(summary = "List faculties", description = "List all faculties.")
    public ResponseEntity<List<FacultyResponse>> listFaculties() {
        return ResponseEntity.ok(facultyService.listFaculties());
    }
}
