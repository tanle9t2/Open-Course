package com.tp.opencourse.service;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.response.CourseLearningResponse;
import com.tp.opencourse.dto.response.CourseResponse;

import java.util.List;

import com.tp.opencourse.dto.response.CourseBasicsResponse;
import com.tp.opencourse.dto.response.CourseFilterResponse;
import com.tp.opencourse.dto.response.PageResponseT;
import com.tp.opencourse.response.MessageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CourseService {
    CourseDTO findById(String id);

    CourseResponse findCourseDetailById(String id);

    List<CourseResponse> findByIds(List<String> courseIds);

    CourseLearningResponse findCourseLearning(String username, String courseId);

    PageResponseT<CourseResponse> findAllBasicsInfo(String keyword, int page, int size, String sortBy, String direction);

    List<CourseFilterResponse> findAllCourseOfTeacher(String teacherId);

    CourseBasicsResponse findBasicsInfoById(String username, String id);

    MessageResponse updateCourse(String username, String id, Map<String, String> fields, MultipartFile file);

    MessageResponse createCourse(String username, Map<String, String> requestCreated);

    PageResponseT<CourseDTO> findByTeacherId(String id, String kw, int page, int limit);
}
