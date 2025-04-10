package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.SectionDTO;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.mapper.CourseMapper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.Optional;

@NoArgsConstructor
public abstract class CourseMapperDecorator implements CourseMapper {
    @Autowired
    private CourseMapper delegate;

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
        courseDTO.getSections().sort(Comparator.comparing(SectionDTO::getCreatedAt));
        return courseDTO;
    }
}
