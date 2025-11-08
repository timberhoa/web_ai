package com.example.web_ai.controller;

import com.example.web_ai.dto.request.CheckAttendanceRequest;
import com.example.web_ai.dto.response.CheckAttendanceResponse;
import com.example.web_ai.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("")
    public ResponseEntity<CheckAttendanceResponse> checkAttendance(@RequestBody CheckAttendanceRequest req){
        return ResponseEntity.ok(attendanceService.checkAttendance(req));
    }
}
