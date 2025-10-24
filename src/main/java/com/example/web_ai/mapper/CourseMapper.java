package com.example.web_ai.mapper;

import com.example.web_ai.dto.response.CourseResponse;
import com.example.web_ai.entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseResponse toResponse(Course course);
}
