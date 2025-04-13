package com.tp.opencourse.service;

import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.dto.response.FilterSearchResponse;
import com.tp.opencourse.dto.response.PageResponse;

import java.util.List;
import java.util.Map;

public interface SearchService {
    PageResponse<CourseResponse> searchCourse(String keyword, String page, String size, Map<String, String> params);
    List<FilterSearchResponse> getAggregation(String keyword);
}
