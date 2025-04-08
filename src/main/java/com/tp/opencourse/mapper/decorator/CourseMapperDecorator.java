package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.mapper.FileMapper;
import com.tp.opencourse.mapper.VideoMapper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public abstract class CourseMapperDecorator implements CourseMapper {
    @Autowired
    private CourseMapper delegate;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private VideoMapper videoMapper;

    @Override
    public CourseDTO convertDTO(Course course) {
        CourseDTO courseDTO = delegate.convertDTO(course);
        Optional.ofNullable(course.getTeacher()).ifPresent(teacher -> {
            CourseDTO.TeacherInfo teacherInfo = CourseDTO.TeacherInfo.builder()
                    .id(teacher.getId())
                    .name(teacher.getFullName())
                    .build();
            courseDTO.setTeacherInfo(teacherInfo);
        });
        return courseDTO;
    }
}
