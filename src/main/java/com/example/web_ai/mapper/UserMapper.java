package com.example.web_ai.mapper;

import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.entity.User;
import com.example.web_ai.entity.Faculty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    // Ignore bidirectional fields when mapping from request to entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "attendances", ignore = true)
    @Mapping(target = "taughtCourses", ignore = true)
    @Mapping(target = "courseLecturers", ignore = true)
    @Mapping(target = "faculty", source = "facultyId", qualifiedByName = "facultyIdToFaculty")
    User toEntity(UserRequest request);
    
    // Map entity to response with simple faculty info
    @Mapping(target = "faculty", source = "faculty", qualifiedByName = "facultyToSimpleResponse")
    UserResponse toResponse(User user);
    
    @Named("facultyIdToFaculty")
    default Faculty facultyIdToFaculty(java.util.UUID facultyId) {
        if (facultyId == null) return null;
        Faculty faculty = new Faculty();
        faculty.setId(facultyId);
        return faculty;
    }
    
    @Named("facultyToSimpleResponse")
    default com.example.web_ai.dto.response.FacultySimpleResponse facultyToSimpleResponse(Faculty faculty) {
        if (faculty == null) return null;
        return com.example.web_ai.dto.response.FacultySimpleResponse.builder()
                .id(faculty.getId())
                .code(faculty.getCode())
                .name(faculty.getName())
                .build();
    }
}


