package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.SectionDTO;
import com.tp.opencourse.dto.response.CourseBasicsResponse;

import com.tp.opencourse.dto.document.CourseDocument;
import com.tp.opencourse.dto.document.SectionDocument;
import com.tp.opencourse.dto.response.CategoryResponse;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.enums.Level;
import com.tp.opencourse.mapper.CategoryMapper;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.repository.CategoryRepository;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.RatingRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public abstract class CourseMapperDecorator implements CourseMapper {
    @Autowired
    private CourseMapper delegate;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CourseResponse convertEntityToResponse(Course course) {
        CategoryResponse categoryInfo = categoryMapper.convertResponse(course.getCategories());

        CourseResponse.TeacherInfo teacherInfo = CourseResponse.TeacherInfo.builder()
                .id(course.getTeacher().getId())
                .name(course.getTeacher().getFullName())
                .build();

        Object[] ratingInfoDetail = ratingRepository.countRatingAverageAndQty(course.getId());

        CourseResponse.RatingInfo ratingInfo = CourseResponse.RatingInfo.builder()
                .average((Double) ratingInfoDetail[1])
                .qty((Long) ratingInfoDetail[0])
                .build();
        long totalLecture = courseRepository.countTotalLecture(course.getId());
        long totalRegistration = courseRepository.countTotalRegistration(course.getId());

        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .price(course.getPrice())
                .totalDuration(course.getTotalDuration())
                .totalLecture(totalLecture)
                .totalRegistration(totalRegistration)
                .level(course.getLevel())
                .banner(course.getBanner())
                .isPublish(course.isPublish())
                .createdAt(course.getCreatedAt())
                .categoryInfo(categoryInfo)
                .teacherInfo(teacherInfo)
                .ratingInfo(ratingInfo)
                .build();
    }

    public CourseDTO convertDTO(Course course) {
        CourseDTO courseDTO = delegate.convertDTO(course);
        Optional.ofNullable(course.getTeacher()).ifPresent(teacher -> {
            CourseDTO.TeacherInfo teacherInfo = CourseDTO.TeacherInfo.builder()
                    .id(teacher.getId())
                    .name(teacher.getFullName())
                    .build();
            courseDTO.setTeacherInfo(teacherInfo);
        });
        Optional.ofNullable(courseDTO.getSections()).ifPresent(s ->
                s.sort(Comparator.comparing(SectionDTO::getCreatedAt)));
        return courseDTO;
    }

    @Override
    public CourseDocument convertEntityToDocument(Course course) {


        List<String> categoryIds = categoryRepository
                .getAllCategoryHierachyIds(
                        course.getCategories().getLft(),
                        course.getCategories().getRgt()
                );

        //category info
        CourseDocument.CategoryDocument categoryInfo = categoryMapper.convertDocument(course.getCategories());
        categoryInfo.setCategoryIds(categoryIds);
        //teacher info
        CourseDocument.TeacherDocument teacherInfo = CourseDocument.TeacherDocument.builder()
                .id(course.getTeacher().getId())
                .name(course.getTeacher().getFullName())
                .build();

        //rating info
        Object[] ratingInfoDetail = ratingRepository.countRatingAverageAndQty(course.getId());
        CourseDocument.RatingDocument ratingInfo = CourseDocument.RatingDocument.builder()
                .average((Double) ratingInfoDetail[1])
                .qty((Long) ratingInfoDetail[0])
                .build();

        //total lecture
        long totalLecture = courseRepository.countTotalLecture(course.getId());

        //total registration
        long totalRegistration = courseRepository.countTotalRegistration(course.getId());

        //section info
        List<SectionDocument> sectionDocumentList = course
                .getSections()
                .stream()
                .map(s -> {
                    List<SectionDocument.ContentDocument> contentList = s.getContentList()
                            .stream().map(c -> SectionDocument.ContentDocument
                                    .builder()
                                    .id(c.getId())
                                    .name(c.getName())
                                    .build())
                            .toList();
                    return SectionDocument
                            .builder()
                            .id(s.getId())
                            .name(s.getName())
                            .contentDocumentList(contentList)
                            .build();
                }).toList();

        return CourseDocument.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .price(course.getPrice())
                .totalDuration(course.getTotalDuration())
                .isPublish(course.isPublish())
                .totalLecture(totalLecture)
                .totalRegistration(totalRegistration)
                .level(course.getLevel())
                .banner(course.getBanner())
                .createdAt(course.getCreatedAt())
                .categoryDocument(categoryInfo)
                .teacherDocument(teacherInfo)
                .ratingDocument(ratingInfo)
                .sectionDocument(sectionDocumentList)
                .build();
    }

    @Override
    public CourseResponse convertDocumentToResponse(CourseDocument course) {
        CourseResponse.TeacherInfo teacherInfo = CourseResponse.TeacherInfo
                .builder()
                .id(course.getTeacherDocument().getId())
                .name(course.getTeacherDocument().getName())
                .build();
        CategoryResponse categoryInfo = CategoryResponse
                .builder()
                .id(course.getCategoryDocument().getId())
                .lft(course.getCategoryDocument().getLft())
                .rgt(course.getCategoryDocument().getRgt())
                .build();
        CourseResponse.RatingInfo ratingInfo = CourseResponse.RatingInfo
                .builder()
                .average(course.getRatingDocument().getAverage())
                .qty(course.getRatingDocument().getQty())
                .build();

        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .price(course.getPrice())
                .isPublish(course.isPublish())
                .totalDuration(course.getTotalDuration())
                .totalLecture(course.getTotalLecture())
                .totalRegistration(course.getTotalRegistration())
                .level(course.getLevel())
                .banner(course.getBanner())
                .createdAt(course.getCreatedAt())
                .categoryInfo(categoryInfo)
                .teacherInfo(teacherInfo)
                .ratingInfo(ratingInfo)
                .build();
    }

    public CourseBasicsResponse convertCourseBasicsResponse(Course course) {
        CourseBasicsResponse response = delegate.convertCourseBasicsResponse(course);
        Optional.ofNullable(course.getCategories()).ifPresent(c -> {
            CourseBasicsResponse.CategoryBasic categoryBasic = CourseBasicsResponse.CategoryBasic.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .build();
            response.setCategory(categoryBasic);
        });
        response.setLevels(List.of(Level.values()));
        return response;
    }
}
