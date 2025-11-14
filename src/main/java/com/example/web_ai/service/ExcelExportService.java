package com.example.web_ai.service;

import com.example.web_ai.dto.response.AttendanceReportResponse;
import com.example.web_ai.dto.response.SessionReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] exportAttendanceReport(AttendanceReportResponse report) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Báo cáo điểm danh");

            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            int rowNum = 0;

            // Title
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(report.getTitle());
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            // Report info
            rowNum = addReportInfo(sheet, rowNum, report, dataStyle);

            // Summary section
            rowNum = addSummarySection(sheet, rowNum, report, headerStyle, dataStyle, numberStyle);

            // Details section
            if (report.getDetails() != null && !report.getDetails().isEmpty()) {
                rowNum++;
                rowNum = addAttendanceDetails(sheet, rowNum, report, headerStyle, dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportSessionReport(SessionReportResponse report) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Báo cáo tiết học");

            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            int rowNum = 0;

            // Title
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(report.getTitle());
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            // Report info
            rowNum = addSessionReportInfo(sheet, rowNum, report, dataStyle);

            // Summary section
            rowNum = addSessionSummarySection(sheet, rowNum, report, headerStyle, dataStyle, numberStyle);

            // Details section
            if (report.getSessions() != null && !report.getSessions().isEmpty()) {
                rowNum++;
                rowNum = addSessionDetails(sheet, rowNum, report, headerStyle, dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < 9; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private int addReportInfo(Sheet sheet, int rowNum, AttendanceReportResponse report, CellStyle style) {
        rowNum++;
        Row row = sheet.createRow(rowNum++);
        createCell(row, 0, "Thông tin báo cáo:", style);
        createCell(row, 1, "", style);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Ngày tạo:", style);
        createCell(row, 1, report.getGeneratedAt() != null ? report.getGeneratedAt().format(DATE_TIME_FORMATTER) : "", style);

        if (report.getCourseName() != null) {
            row = sheet.createRow(rowNum++);
            createCell(row, 0, "Môn học:", style);
            createCell(row, 1, report.getCourseCode() + " - " + report.getCourseName(), style);
        }

        if (report.getStudentName() != null) {
            row = sheet.createRow(rowNum++);
            createCell(row, 0, "Sinh viên:", style);
            createCell(row, 1, report.getStudentName(), style);
        }

        if (report.getFromDate() != null && report.getToDate() != null) {
            row = sheet.createRow(rowNum++);
            createCell(row, 0, "Từ ngày:", style);
            createCell(row, 1, report.getFromDate().format(DATE_FORMATTER), style);
            createCell(row, 2, "Đến ngày:", style);
            createCell(row, 3, report.getToDate().format(DATE_FORMATTER), style);
        }

        return rowNum;
    }

    private int addSessionReportInfo(Sheet sheet, int rowNum, SessionReportResponse report, CellStyle style) {
        rowNum++;
        Row row = sheet.createRow(rowNum++);
        createCell(row, 0, "Thông tin báo cáo:", style);
        createCell(row, 1, "", style);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Ngày tạo:", style);
        createCell(row, 1, report.getGeneratedAt() != null ? report.getGeneratedAt().format(DATE_TIME_FORMATTER) : "", style);

        if (report.getCourseName() != null) {
            row = sheet.createRow(rowNum++);
            createCell(row, 0, "Môn học:", style);
            createCell(row, 1, report.getCourseCode() + " - " + report.getCourseName(), style);
        }

        if (report.getTeacherName() != null) {
            row = sheet.createRow(rowNum++);
            createCell(row, 0, "Giảng viên:", style);
            createCell(row, 1, report.getTeacherName(), style);
        }

        if (report.getFromDate() != null && report.getToDate() != null) {
            row = sheet.createRow(rowNum++);
            createCell(row, 0, "Từ ngày:", style);
            createCell(row, 1, report.getFromDate().format(DATE_FORMATTER), style);
            createCell(row, 2, "Đến ngày:", style);
            createCell(row, 3, report.getToDate().format(DATE_FORMATTER), style);
        }

        return rowNum;
    }

    private int addSummarySection(Sheet sheet, int rowNum, AttendanceReportResponse report,
                                   CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle) {
        rowNum++;
        Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "Thống kê tổng quan", headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 3));

        Row row = sheet.createRow(rowNum++);
        createCell(row, 0, "Tổng số tiết học:", dataStyle);
        createCell(row, 1, report.getTotalSessions() != null ? report.getTotalSessions().toString() : "0", numberStyle);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Tổng số sinh viên:", dataStyle);
        createCell(row, 1, report.getTotalStudents() != null ? report.getTotalStudents().toString() : "0", numberStyle);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Có mặt:", dataStyle);
        createCell(row, 1, report.getPresentCount() != null ? report.getPresentCount().toString() : "0", numberStyle);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Đi muộn:", dataStyle);
        createCell(row, 1, report.getLateCount() != null ? report.getLateCount().toString() : "0", numberStyle);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Vắng mặt:", dataStyle);
        createCell(row, 1, report.getAbsentCount() != null ? report.getAbsentCount().toString() : "0", numberStyle);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Có phép:", dataStyle);
        createCell(row, 1, report.getExcusedCount() != null ? report.getExcusedCount().toString() : "0", numberStyle);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Tỉ lệ điểm danh:", dataStyle);
        createCell(row, 1, report.getAttendanceRate() != null ? String.format("%.2f%%", report.getAttendanceRate()) : "0%", numberStyle);

        return rowNum;
    }

    private int addSessionSummarySection(Sheet sheet, int rowNum, SessionReportResponse report,
                                         CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle) {
        rowNum++;
        Row headerRow = sheet.createRow(rowNum++);
        createCell(headerRow, 0, "Thống kê tổng quan", headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 3));

        Row row = sheet.createRow(rowNum++);
        createCell(row, 0, "Tổng số tiết học:", dataStyle);
        createCell(row, 1, report.getTotalSessions() != null ? report.getTotalSessions().toString() : "0", numberStyle);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Tổng số sinh viên:", dataStyle);
        createCell(row, 1, report.getTotalStudents() != null ? report.getTotalStudents().toString() : "0", numberStyle);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Tổng có mặt:", dataStyle);
        createCell(row, 1, report.getTotalPresent() != null ? report.getTotalPresent().toString() : "0", numberStyle);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Tổng đi muộn:", dataStyle);
        createCell(row, 1, report.getTotalLate() != null ? report.getTotalLate().toString() : "0", numberStyle);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Tổng vắng mặt:", dataStyle);
        createCell(row, 1, report.getTotalAbsent() != null ? report.getTotalAbsent().toString() : "0", numberStyle);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Tổng có phép:", dataStyle);
        createCell(row, 1, report.getTotalExcused() != null ? report.getTotalExcused().toString() : "0", numberStyle);

        row = sheet.createRow(rowNum++);
        createCell(row, 0, "Tỉ lệ điểm danh trung bình:", dataStyle);
        createCell(row, 1, report.getAverageAttendanceRate() != null ? String.format("%.2f%%", report.getAverageAttendanceRate()) : "0%", numberStyle);

        return rowNum;
    }

    private int addAttendanceDetails(Sheet sheet, int rowNum, AttendanceReportResponse report,
                                      CellStyle headerStyle, CellStyle dataStyle) {
        rowNum++;
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"STT", "Tiết học", "Thời gian bắt đầu", "Thời gian kết thúc", "Phòng", "Sinh viên", "Email", "Trạng thái", "Thời gian điểm danh", "Ghi chú"};
        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i], headerStyle);
        }

        int stt = 1;
        for (AttendanceReportResponse.AttendanceReportDetailItem item : report.getDetails()) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, String.valueOf(stt++), dataStyle);
            createCell(row, 1, item.getSessionId() != null ? item.getSessionId().toString().substring(0, 8) : "", dataStyle);
            createCell(row, 2, item.getSessionStartTime() != null ? item.getSessionStartTime().format(DATE_TIME_FORMATTER) : "", dataStyle);
            createCell(row, 3, item.getSessionEndTime() != null ? item.getSessionEndTime().format(DATE_TIME_FORMATTER) : "", dataStyle);
            createCell(row, 4, item.getRoomName() != null ? item.getRoomName() : "", dataStyle);
            createCell(row, 5, item.getStudentName() != null ? item.getStudentName() : "", dataStyle);
            createCell(row, 6, item.getStudentEmail() != null ? item.getStudentEmail() : "", dataStyle);
            createCell(row, 7, item.getStatus() != null ? item.getStatus() : "", dataStyle);
            createCell(row, 8, item.getCheckedAt() != null ? item.getCheckedAt().format(DATE_TIME_FORMATTER) : "", dataStyle);
            createCell(row, 9, item.getNote() != null ? item.getNote() : "", dataStyle);
        }

        return rowNum;
    }

    private int addSessionDetails(Sheet sheet, int rowNum, SessionReportResponse report,
                                  CellStyle headerStyle, CellStyle dataStyle) {
        rowNum++;
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"STT", "Mã tiết học", "Thời gian bắt đầu", "Thời gian kết thúc", "Phòng", "Đã khóa", "Tổng SV", "Có mặt", "Đi muộn", "Vắng", "Có phép", "Tỉ lệ"};
        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i], headerStyle);
        }

        int stt = 1;
        for (SessionReportResponse.SessionReportDetailItem item : report.getSessions()) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, String.valueOf(stt++), dataStyle);
            createCell(row, 1, item.getSessionId() != null ? item.getSessionId().toString().substring(0, 8) : "", dataStyle);
            createCell(row, 2, item.getStartTime() != null ? item.getStartTime().format(DATE_TIME_FORMATTER) : "", dataStyle);
            createCell(row, 3, item.getEndTime() != null ? item.getEndTime().format(DATE_TIME_FORMATTER) : "", dataStyle);
            createCell(row, 4, item.getRoomName() != null ? item.getRoomName() : "", dataStyle);
            createCell(row, 5, item.isLocked() ? "Có" : "Không", dataStyle);
            createCell(row, 6, item.getTotalEnrolled() != null ? item.getTotalEnrolled().toString() : "0", dataStyle);
            createCell(row, 7, item.getPresentCount() != null ? item.getPresentCount().toString() : "0", dataStyle);
            createCell(row, 8, item.getLateCount() != null ? item.getLateCount().toString() : "0", dataStyle);
            createCell(row, 9, item.getAbsentCount() != null ? item.getAbsentCount().toString() : "0", dataStyle);
            createCell(row, 10, item.getExcusedCount() != null ? item.getExcusedCount().toString() : "0", dataStyle);
            createCell(row, 11, item.getAttendanceRate() != null ? String.format("%.2f%%", item.getAttendanceRate()) : "0%", dataStyle);
        }

        return rowNum;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}

