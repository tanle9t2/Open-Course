package com.tp.opencourse.controller;

import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.dto.response.FilterSearchResponse;
import com.tp.opencourse.dto.response.PageResponse;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.SearchService;
import com.tp.opencourse.utils.APIResponseMessage;
import com.tp.opencourse.utils.FilterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Filter
//Video duration
//Category
//Level
//Rating
//Teacher
//minPrice
//maxPrice

//Sort
//Most relevant
//Highest rated
//Newest
//Most relevant
//Descending price
//Increasing price


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SearchRestController {

    private final SearchService searchService;


    @GetMapping("/search/aggs")
    public ResponseEntity<?> searchCourses(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword
    ) {
        List<FilterSearchResponse> results = searchService.getAggregation(keyword);
        MessageResponse messageResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(results)
                .build();
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @GetMapping("/search/courses")
    public ResponseEntity<?> searchCourses(
        @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
        @RequestParam(value = "page", required = false, defaultValue = FilterUtils.PAGE) String page,
        @RequestParam(value = "size", required = false, defaultValue = FilterUtils.PAGE_SIZE) String size,
        @RequestParam(value = "sortBy", required = false, defaultValue = "") String sortBy,
        @RequestParam(value = "order", required = false, defaultValue = "") String order,

        @RequestParam(value = "category", required = false, defaultValue = "") String categories,
        @RequestParam(value = "level", required = false, defaultValue = "") String level,
        @RequestParam(value = "duration", required = false, defaultValue = "") String duration,
        @RequestParam(value = "rating", required = false, defaultValue = "") String rating,
        @RequestParam(value = "teacher", required = false, defaultValue = "") String teacher,
        @RequestParam(value = "minPrice", required = false, defaultValue = "") String minPrice,
        @RequestParam(value = "maxPrice", required = false, defaultValue = "") String maxPrice
    ) {
        Map<String, String> params = new HashMap<>() {{
            put("sortBy", sortBy);
            put("order", order);
            put("categories", categories);
            put("level", level);
            put("duration", duration);
            put("rating", rating);
            put("teacher", teacher);
            put("minPrice", minPrice);
            put("maxPrice", maxPrice);
        }};
        PageResponse<CourseResponse> courses = searchService.searchCourse(keyword, page, size, params);
        MessageResponse messageResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(courses)
                .build();
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

}




















































