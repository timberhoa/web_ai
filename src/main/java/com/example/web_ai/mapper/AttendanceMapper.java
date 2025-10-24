package com.example.web_ai.mapper;

import com.example.web_ai.dto.request.CheckAttendanceRequest;
import com.example.web_ai.dto.response.CheckAttendanceResponse;
import com.example.web_ai.entity.Attendance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    Attendance toEntity(CheckAttendanceRequest request);
    CheckAttendanceResponse toResponse(Attendance attendance);
}