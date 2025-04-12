package com.tp.opencourse.service;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.reponse.CourseBasicsResponse;
import com.tp.opencourse.dto.reponse.CourseFilterResponse;
import com.tp.opencourse.dto.reponse.PageResponse;
import com.tp.opencourse.response.MessageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CourseService {
    CourseDTO findById(String id);

    List<CourseFilterResponse> findAllCourseOfTeacher(String teacherId);

    CourseBasicsResponse findBasicsInfoById(String id);

    MessageResponse updateCourse(String id, Map<String, String> fields, MultipartFile file);

    MessageResponse createCourse(Map<String, String> requestCreated);

    PageResponse<CourseDTO> findByTeacherId(String id, int page, int limit);
}
