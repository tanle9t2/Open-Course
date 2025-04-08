package com.tp.opencourse.service;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.reponse.PageResponse;
import com.tp.opencourse.response.MessageResponse;

import java.util.Map;

public interface CourseService {
    CourseDTO findById(String id);

    MessageResponse createCourse(Map<String, String> requestCreated);



    PageResponse<CourseDTO> findByTeacherId(String id, int page, int limit);
}
