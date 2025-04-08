package com.tp.opencourse.mapper.decorator;


import com.tp.opencourse.dto.response.CategoryResponse;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.mapper.CategoryMapper;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.mapper.SubmitionMapper;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.RatingRepository;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
@Mapper
public abstract class CourseMapperDecorator implements CourseMapper {
    @Autowired
    private CourseMapper delegate;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public CourseResponse convertEntityToResponse(Course course) {
        CategoryResponse categoryInfo = categoryMapper.convertResponse(course.getCategories());

        CourseResponse.TeacherInfo teacherInfo =  CourseResponse.TeacherInfo.builder()
                .id(course.getTeacher().getId())
                .name(course.getTeacher().getFullName())
                .build();

        Object[] ratingInfoDetail = ratingRepository.countRatingAverageAndQty(course.getId());

        CourseResponse.RatingInfo ratingInfo = CourseResponse.RatingInfo.builder()
                .average((Double) ratingInfoDetail[1])
                .qty((Long) ratingInfoDetail[0])
                .build();

        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .price(course.getPrice())
                .totalDuration(course.getTotalDuration())
                .totalLecture(courseRepository.countTotalLecture(course.getId()))
                .level(course.getLevel())
                .banner(course.getBanner())
                .createdAt(course.getCreatedAt())
                .categoryInfo(categoryInfo)
                .teacherInfo(teacherInfo)
                .ratingInfo(ratingInfo)
                .build();
    }
}
