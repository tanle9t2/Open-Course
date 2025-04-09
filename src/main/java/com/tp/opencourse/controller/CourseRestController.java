package com.tp.opencourse.controller;

import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseRestController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/multiple")
    public ResponseEntity<MessageResponse> getCoursesByIds(@RequestParam("courseIds") String courseIds) {
        List<String> courseIdArray = Arrays.asList(courseIds.split(","));
        var data = courseService.findByIds(courseIdArray);
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Deleted successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }



}
