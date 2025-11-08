package com.example.web_ai.mapper;

import com.example.web_ai.dto.request.CheckAttendanceRequest;
import com.example.web_ai.dto.response.CheckAttendanceResponse;
import com.example.web_ai.entity.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    
    // Ignore fields that will be set manually in service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "session", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "checkedAt", ignore = true)
    Attendance toEntity(CheckAttendanceRequest request);
    
    // Map attendance ID to response
    @Mapping(target = "attendance_id", source = "id")
    @Mapping(target = "message", ignore = true)
    @Mapping(target = "studentName", ignore = true)
    CheckAttendanceResponse toResponse(Attendance attendance);
}