package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.dto.response.RegisterResponse;
import com.tp.opencourse.entity.*;
import com.tp.opencourse.entity.enums.PaymentStatus;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.dto.Page;
import com.tp.opencourse.dto.reponse.CourseBasicsResponse;
import com.tp.opencourse.dto.reponse.CourseFilterResponse;
import com.tp.opencourse.dto.reponse.PageResponse;
import com.tp.opencourse.entity.enums.Level;
import com.tp.opencourse.exceptions.BadRequestException;
import com.tp.opencourse.repository.CategoryRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CloudinaryService;
import com.tp.opencourse.service.CourseService;
import com.tp.opencourse.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.tp.opencourse.utils.Helper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CloudinaryService cloudinaryService;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CourseMapper courseMapper;

    @Override
    public CourseDTO findById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));

        return courseMapper.convertDTO(course);
    }
    public CourseResponse findCourseDetailById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));
        return courseMapper.convertEntityToResponse(course);
    }

    @Override
    public List<CourseFilterResponse> findAllCourseOfTeacher(String teacherId) {
        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        return courses.stream()
                .map(c -> CourseFilterResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CourseBasicsResponse findBasicsInfoById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));

        return courseMapper.convertCourseBasicsResponse(course);
    }

    @Override
    public MessageResponse updateCourse(String id, Map<String, String> fields, MultipartFile file) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));
        Optional.ofNullable(fields.get("price")).ifPresent(price -> course.setPrice(Double.parseDouble(price)));
        Optional.ofNullable(fields.get("name")).ifPresent(name -> course.setName(name));
        Optional.ofNullable(fields.get("description")).ifPresent(description -> course.setDescription(description));
        Optional.ofNullable(fields.get("level")).ifPresent(level -> course.setLevel(Level.valueOf(level)));
        Optional.ofNullable(fields.get("category")).ifPresent(category -> {
            Category c = categoryRepository.findById(category)
                    .orElseThrow(() -> new ResourceNotFoundExeption("Not found category"));
            course.setCategories(c);
        });
        Optional.ofNullable(file).ifPresent(f -> {

            try {
                if (course.getBanner() != null) {
                    cloudinaryService.removeResource(course.getBanner(), "image");
                }
                String url = cloudinaryService.uploadImage(f);
                course.setBanner(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return MessageResponse.builder()
                .data(null)
                .message("Successfully update course")
                .status(HttpStatus.OK)
                .build();
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
                .categories(category)
                .build();
        course = courseRepository.create(course);

        return MessageResponse.builder()
                .message("Successfully create course")
                .data(courseMapper.convertDTO(course))
                .status(HttpStatus.CREATED)
                .build();
    }

    @Override
    public PageResponse<CourseDTO> findByTeacherId(String id, String kw, int page, int limit) {
        Page<Course> coursePage = courseRepository.findByTeacherId(id, kw, page, limit);

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

    @Override
    public List<CourseResponse> findByIds(List<String> courseIds) {
        List<Course> courseResponses = courseRepository.findAllByIds(new HashSet<>(courseIds));
        if(courseResponses.size() != courseIds.size()) {
            throw new ResourceNotFoundExeption("Invalid course ids");
        }

        return courseResponses.stream().map(courseMapper::convertEntityToResponse)
                .collect(Collectors.toList());
    }

}
