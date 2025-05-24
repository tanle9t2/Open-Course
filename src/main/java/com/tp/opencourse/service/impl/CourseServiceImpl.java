package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.*;
import com.tp.opencourse.dto.response.CourseLearningResponse;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.entity.*;
import com.tp.opencourse.entity.enums.CourseStatus;
import com.tp.opencourse.exceptions.AccessDeniedException;
import com.tp.opencourse.exceptions.BadRequestException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.*;
import com.tp.opencourse.repository.*;
import com.tp.opencourse.dto.response.CourseBasicsResponse;
import com.tp.opencourse.dto.response.CourseFilterResponse;
import com.tp.opencourse.dto.response.PageResponseT;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.enums.Level;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.CategoryRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CloudinaryService;
import com.tp.opencourse.service.CourseService;
import com.tp.opencourse.utils.FilterUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.tp.opencourse.utils.Helper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
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
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CourseMapper courseMapper;
    private final ContentMapper contentMapper;
    private final ContentProcessMapper contentProcessMapper;

    @Override
    public CourseDTO findById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));

        return courseMapper.convertDTO(course);
    }

    @Override
    public MessageResponse updateStatus(String id, String status) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));

        course.setStatus(CourseStatus.valueOf(status));

        courseRepository.update(course);
        return MessageResponse.builder()
                .message("Successfully update course")
                .status(HttpStatus.OK)
                .build();
    }

    @Override
    public MessageResponse deleteCourseById(String username, String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));
        if (!course.getTeacher().getUsername().equals(username))
            throw new AccessDeniedException("You don't have permission for this resource");

        course.setStatus(CourseStatus.DELETE);
        courseRepository.update(course);

        return MessageResponse.builder()
                .status(HttpStatus.OK)
                .data(Map.of("id", id))
                .message("Successfully delete course")
                .build();
    }

    public CourseResponse findCourseDetailById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));

        if (!course.isPublish() || !course.getStatus().equals(CourseStatus.ACTIVE)) {
            throw new BadRequestException("Course is not published");
        }

        return courseMapper.convertEntityToResponse(course);
    }

    @Override
    public PageResponseT<CourseResponse> findAllBasicsInfo(String keyword, int page, int size, String sortBy, String direction) {


        Page<Course> coursePage = courseRepository.findAll(keyword, page, size, sortBy, direction);

        return PageResponseT.<CourseResponse>builder()
                .count((long) coursePage.getContent().size())
                .page(page)
                .totalElement((int) coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .data(coursePage.getContent().stream()
                        .map(c -> courseMapper.convertEntityToResponse(c))
                        .collect(Collectors.toList()))
                .status(HttpStatus.OK)
                .build();
    }

    @Override
    public PageResponseT<CourseResponse> findAllByActive(String keyword, int page, int size, String sortBy, String direction) {
        Page<Course> coursePage = courseRepository.findAllInActive(keyword, page, size, sortBy, direction);

        return PageResponseT.<CourseResponse>builder()
                .count((long) coursePage.getContent().size())
                .page(page)
                .totalElement((int) coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .data(coursePage.getContent().stream()
                        .map(c -> courseMapper.convertEntityToResponse(c))
                        .collect(Collectors.toList()))
                .status(HttpStatus.OK)
                .build();
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
    public CourseBasicsResponse findBasicsInfoById(String username, String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));


        return courseMapper.convertCourseBasicsResponse(course);
    }

    @Override
    public MessageResponse updateCourse(String username, String id, Map<String, String> fields, MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));
        Optional.ofNullable(course).ifPresent(c -> {
            if (!c.getTeacher().getUsername().equals(username))
                throw new AccessDeniedException("You don't have permission for this resource");
        });
        Optional.ofNullable(fields.get("publish")).ifPresent(publish -> course.setPublish(Boolean.parseBoolean(publish)));
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
                response.put("banner", url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return MessageResponse.builder()
                .data(response)
                .message("Successfully update course")
                .status(HttpStatus.OK)
                .build();
    }

    @Override
    public MessageResponse createCourse(String username, Map<String, String> requestCreated) {
        Helper.validateRequiredFields(requestCreated, "name", "categoryId");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found teacher"));

        Category category = categoryRepository.findById(requestCreated.get("categoryId"))
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found category"));


        Course course = Course.builder()
                .name(requestCreated.get("name"))
                .isPublish(false)
                .teacher(user)
                .status(CourseStatus.PENDING)
                .banner(FilterUtils.DEFAULT_BANNER)
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
    public PageResponseT<CourseDTO> findByTeacherId(String id, String kw, int page, int limit) {
        Page<Course> coursePage = courseRepository.findByTeacherId(id, kw, page, limit);

        return PageResponseT.<CourseDTO>builder()
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
        if (courseResponses.size() != courseIds.size()) {
            throw new ResourceNotFoundExeption("Invalid course ids");
        }

        return courseResponses.stream().map(courseMapper::convertEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CourseLearningResponse findCourseLearning(String username, String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found course"));

        List<CourseLearningResponse.SectionInfo> sectionInfos = course.getSections().stream()
                .map(s -> {
                    Set<ContentProcessDTO> contentProcesses = contentRepository.countContentComplete(s.getId(), username)
                            .stream()
                            .map(c -> contentProcessMapper.convertDTO(c))
                            .collect(Collectors.toSet());

                    for (Content c : s.getContentList()) {
                        ContentProcessDTO contentProcessDTO = ContentProcessDTO.builder()
                                .content(contentMapper.convertDTO(c))
                                .watchedTime(0)
                                .status(false)
                                .build();
                        contentProcesses.add(contentProcessDTO);
                    }

                    double totalDuration = s.getContentList().stream()
                            .mapToDouble(c -> c.getResource() instanceof Video ? ((Video) c.getResource()).getDuration() : 60)
                            .sum();

                    long completedLecture = contentProcesses.stream()
                            .filter(c -> c.isStatus())
                            .count();

                    return CourseLearningResponse.SectionInfo.builder()
                            .id(s.getId())
                            .name(s.getName())
                            .completedLecture((int) completedLecture)
                            .createdAt(s.getCreatedAt())
                            .lectures(contentProcesses)
                            .totalDuration(totalDuration)
                            .build();
                }).sorted(Comparator.comparing(CourseLearningResponse.SectionInfo::getCreatedAt))
                .collect(Collectors.toList());

        return CourseLearningResponse.builder()
                .sections(sectionInfos)
                .id(courseId)
                .name(course.getName())
                .build();
    }
}
