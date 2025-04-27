package com.tp.opencourse.controller;

import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CourseService;
import com.tp.opencourse.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseRestController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private SectionService sectionService;

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

    @GetMapping("/{courseId}")
    public ResponseEntity<MessageResponse> getCourse(@PathVariable("courseId") String courseId) {
        var data = courseService.findCourseDetailById(courseId);
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Deleted successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{courseId}/section")
    public ResponseEntity<MessageResponse> getCourseContent(@PathVariable("courseId") String courseId) {
        var data = sectionService.findByCourseId(courseId);
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Deleted successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
