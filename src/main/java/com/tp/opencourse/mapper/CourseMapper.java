package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.mapper.decorator.CourseMapperDecorator;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@DecoratedWith(CourseMapperDecorator.class)
public interface CourseMapper {
    CourseDTO convertDTO(Course course);
    Course convertEntity(CourseDTO courseDTO);
    CourseResponse convertEntityToResponse(Course course);
}
