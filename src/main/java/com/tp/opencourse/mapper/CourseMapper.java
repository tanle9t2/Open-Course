package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.document.CourseDocument;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.dto.reponse.CourseBasicsResponse;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.mapper.decorator.CourseMapperDecorator;
import org.mapstruct.DecoratedWith;
import com.tp.opencourse.mapper.decorator.CommentMapperDecorator;
import com.tp.opencourse.mapper.decorator.CourseMapperDecorator;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, SectionMapper.class})
@DecoratedWith(CourseMapperDecorator.class)
public interface CourseMapper {
    CourseDTO convertDTO(Course course);
    Course convertEntity(CourseDTO courseDTO);
    CourseResponse convertEntityToResponse(Course course);
    CourseDocument convertEntityToDocument(Course course);
    CourseResponse convertDocumentToResponse(CourseDocument course);

    CourseBasicsResponse convertCourseBasicsResponse(Course course);
}
