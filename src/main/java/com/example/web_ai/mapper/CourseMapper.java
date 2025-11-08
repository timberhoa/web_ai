package com.example.web_ai.mapper;

import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    
    @Mapping(target = "teacher_id", expression = "java(course.getTeacher() != null ? course.getTeacher().getId() : null)")
    @Mapping(target = "teacher_name", expression = "java(course.getTeacher() != null ? course.getTeacher().getFullName() : null)")
    @Mapping(target = "faculty_code", expression = "java(course.getFaculty() != null ? course.getFaculty().getCode() : null)")
    @Mapping(target = "faculty_name", expression = "java(course.getFaculty() != null ? course.getFaculty().getName() : null)")
    CourseResponse toResponse(Course course);
}
