package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseDTO convertDTO(Course course);

    Course convertEntity(CourseDTO courseDTO);
}
