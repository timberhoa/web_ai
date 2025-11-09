package com.example.web_ai.mapper;

import com.example.web_ai.dto.request.CheckAttendanceRequest;
import com.example.web_ai.dto.response.CheckAttendanceResponse;
import com.example.web_ai.entity.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public Attendance toEntity(CheckAttendanceRequest request) {
        if (request == null)
            return null;
        Attendance a = new Attendance();
        // id/session/student/checkedAt are set in service layer
        a.setStatus(request.getStatus());
        a.setStudentLat(request.getStudentLat());
        a.setStudentLng(request.getStudentLng());
        a.setNote(request.getNote());
        return a;
    }

    public CheckAttendanceResponse toResponse(Attendance attendance) {
        if (attendance == null)
            return null;
        CheckAttendanceResponse res = new CheckAttendanceResponse();
        res.setAttendance_id(attendance.getId());
        // message and studentName are set in service layer
        return res;
    }
}
