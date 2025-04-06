package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.Page;
import com.tp.opencourse.dto.reponse.PageResponse;
import com.tp.opencourse.entity.Category;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.exceptions.BadRequestException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.repository.CategoryRepository;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CourseService;
import com.tp.opencourse.utils.Helper;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public CourseDTO findById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));

        return courseMapper.convertDTO(course);
    }

    @Override
    public MessageResponse createCourse(Map<String, String> requestCreated) {
        Helper.validateRequiredFields(requestCreated, "name", "teacherId", "categoryId");
        User user = userRepository.findById(requestCreated.get("teacherId"))
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found teacher"));
        Category category = categoryRepository.findById(requestCreated.get("categoryId"))
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found category"));
        Course course = Course.builder()
                .name(requestCreated.get("name"))
                .isPublish(false)
                .teacher(user)
                .createdAt(LocalDateTime.now())
                .category(category)
                .build();
        course = courseRepository.create(course);

        return MessageResponse.builder()
                .message("Successfully create course")
                .data(courseMapper.convertDTO(course))
                .status(HttpStatus.CREATED)
                .build();
    }

    @Override
    public PageResponse<CourseDTO> findByTeacherId(String id, int page, int limit) {
        Page<Course> coursePage = courseRepository.findByTeacherId(id, page, limit);

        return PageResponse.<CourseDTO>builder()
                .count((long) coursePage.getContent().size())
                .page(page)
                .totalElement((int) coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .data(coursePage.getContent().stream()
                        .map(c -> courseMapper.convertDTO(c))
                        .collect(Collectors.toList()))
                .status(HttpStatus.OK)
                .build();
    }
}
