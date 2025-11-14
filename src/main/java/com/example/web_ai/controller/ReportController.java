package com.example.web_ai.controller;

import com.example.web_ai.dto.request.ReportFilterRequest;
import com.example.web_ai.dto.response.AttendanceReportResponse;
import com.example.web_ai.dto.response.ReportListResponse;
import com.example.web_ai.dto.response.SessionReportResponse;
import com.example.web_ai.service.ExcelExportService;
import com.example.web_ai.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Generate and download attendance and session reports")
public class ReportController {

    private final ReportService reportService;
    private final ExcelExportService excelExportService;

    @GetMapping("/attendance")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Generate attendance report",
            description = "Generate a detailed attendance report based on filters (course, session, student, date range)")
    public ResponseEntity<AttendanceReportResponse> generateAttendanceReport(
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "sessionId", required = false) UUID sessionId,
            @RequestParam(value = "studentId", required = false) UUID studentId,
            @RequestParam(value = "fromDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(value = "toDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(value = "includeDetails", defaultValue = "true") Boolean includeDetails,
            Authentication authentication) {
        
        String username = authentication.getName();
        String generatedBy = username;

        ReportFilterRequest filter = ReportFilterRequest.builder()
            .reportType("ATTENDANCE")
            .courseId(courseId)
            .sessionId(sessionId)
            .studentId(studentId)
            .fromDate(fromDate)
            .toDate(toDate)
            .includeDetails(includeDetails)
            .build();

        AttendanceReportResponse report = reportService.generateAttendanceReport(filter, generatedBy);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Generate session report",
            description = "Generate a report of class sessions with attendance statistics")
    public ResponseEntity<SessionReportResponse> generateSessionReport(
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "teacherId", required = false) UUID teacherId,
            @RequestParam(value = "fromDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(value = "toDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            Authentication authentication) {
        
        String username = authentication.getName();
        String generatedBy = username;

        ReportFilterRequest filter = ReportFilterRequest.builder()
            .reportType("SESSION")
            .courseId(courseId)
            .teacherId(teacherId)
            .fromDate(fromDate)
            .toDate(toDate)
            .includeDetails(true)
            .build();

        SessionReportResponse report = reportService.generateSessionReport(filter, generatedBy);
        return ResponseEntity.ok(report);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "List available reports",
            description = "List all available reports with pagination and filtering")
    public ResponseEntity<Page<ReportListResponse>> listReports(
            @RequestParam(value = "reportType", required = false) String reportType,
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @PageableDefault(size = 20, sort = "generatedAt") Pageable pageable) {
        
        ReportFilterRequest filter = ReportFilterRequest.builder()
            .reportType(reportType)
            .courseId(courseId)
            .build();

        Page<ReportListResponse> reports = reportService.listReports(filter, pageable);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/attendance/{reportId}/download")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Download attendance report as Excel",
            description = "Download the attendance report in Excel format (.xlsx)")
    public ResponseEntity<byte[]> downloadAttendanceReportExcel(
            @PathVariable UUID reportId,
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "sessionId", required = false) UUID sessionId,
            @RequestParam(value = "studentId", required = false) UUID studentId,
            @RequestParam(value = "fromDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(value = "toDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            Authentication authentication) {
        
        String username = authentication.getName();

        ReportFilterRequest filter = ReportFilterRequest.builder()
            .reportType("ATTENDANCE")
            .courseId(courseId)
            .sessionId(sessionId)
            .studentId(studentId)
            .fromDate(fromDate)
            .toDate(toDate)
            .includeDetails(true)
            .build();

        AttendanceReportResponse report = reportService.generateAttendanceReport(filter, username);
        
        try {
            byte[] excelData = excelExportService.exportAttendanceReport(report);
            
            String filename = String.format("BaoCaoDiemDanh_%s.xlsx", 
                report.getGeneratedAt() != null 
                    ? report.getGeneratedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    : java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);

            return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
        } catch (Exception e) {
            log.error("Error generating Excel file", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sessions/{reportId}/download")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Download session report as Excel",
            description = "Download the session report in Excel format (.xlsx)")
    public ResponseEntity<byte[]> downloadSessionReportExcel(
            @PathVariable UUID reportId,
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "teacherId", required = false) UUID teacherId,
            @RequestParam(value = "fromDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(value = "toDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            Authentication authentication) {
        
        String username = authentication.getName();

        ReportFilterRequest filter = ReportFilterRequest.builder()
            .reportType("SESSION")
            .courseId(courseId)
            .teacherId(teacherId)
            .fromDate(fromDate)
            .toDate(toDate)
            .includeDetails(true)
            .build();

        SessionReportResponse report = reportService.generateSessionReport(filter, username);
        
        try {
            byte[] excelData = excelExportService.exportSessionReport(report);
            
            String filename = String.format("BaoCaoTietHoc_%s.xlsx", 
                report.getGeneratedAt() != null 
                    ? report.getGeneratedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    : java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);

            return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
        } catch (Exception e) {
            log.error("Error generating Excel file", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/attendance/generate")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Generate attendance report with request body",
            description = "Generate attendance report using a request body for more complex filters")
    public ResponseEntity<AttendanceReportResponse> generateAttendanceReportPost(
            @Valid @RequestBody ReportFilterRequest filter,
            Authentication authentication) {
        
        String username = authentication.getName();
        AttendanceReportResponse report = reportService.generateAttendanceReport(filter, username);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/sessions/generate")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Generate session report with request body",
            description = "Generate session report using a request body for more complex filters")
    public ResponseEntity<SessionReportResponse> generateSessionReportPost(
            @Valid @RequestBody ReportFilterRequest filter,
            Authentication authentication) {
        
        String username = authentication.getName();
        SessionReportResponse report = reportService.generateSessionReport(filter, username);
        return ResponseEntity.ok(report);
    }
}

