package com.tp.opencourse.service;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.response.CourseResponse;

import java.util.List;

public interface CourseService {
    CourseDTO findById(String id);
    List<CourseResponse> findByIds(List<String> courseIds);
}
