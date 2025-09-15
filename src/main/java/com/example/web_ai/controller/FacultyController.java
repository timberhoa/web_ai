package com.example.web_ai.controller;

import com.example.web_ai.dto.request.FacultyRequest;
import com.example.web_ai.dto.response.FacultyResponse;
import com.example.web_ai.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/faculties")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @PostMapping
    public ResponseEntity<FacultyResponse> addFaculty(@RequestBody FacultyRequest req) {
        return ResponseEntity.ok(facultyService.addFaculty(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacultyResponse> updateFaculty(@PathVariable UUID id,
                                                         @RequestBody FacultyRequest req) {
        return ResponseEntity.ok(facultyService.updateFaculty(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable UUID id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponse> getFaculty(@PathVariable UUID id) {
        return ResponseEntity.ok(facultyService.getFaculty(id));
    }
    @GetMapping
    public ResponseEntity<List<FacultyResponse>> listFaculties() {
        return ResponseEntity.ok(facultyService.listFaculties());
    }
}
