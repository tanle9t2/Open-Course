package com.tp.opencourse.controller;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.reponse.CourseBasicsResponse;
import com.tp.opencourse.dto.reponse.CourseFilterResponse;
import com.tp.opencourse.dto.reponse.PageResponse;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CourseService;
import com.tp.opencourse.utils.FilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyAuthority('TEACHER')")
public class CourseRestController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/course")
    public ResponseEntity<MessageResponse> createCourse(
            Principal user,
            @RequestBody Map<String, String> request) {
        MessageResponse response = courseService.createCourse(user.getName(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/course/{courseId}")
    public ResponseEntity<MessageResponse> updateBaisicInfo(
            Principal user,
            @PathVariable("courseId") String id,
            @RequestParam Map<String, String> field,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        MessageResponse messageResponse = courseService.updateCourse(user.getName(), id, field, file);
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable("courseId") String courseId) {
        CourseDTO courseDTO = courseService.findById(courseId);
        return ResponseEntity.ok(courseDTO);
    }

    @GetMapping("/courses/filter/{teacherId}")
    public ResponseEntity<List<CourseFilterResponse>> getAllCourse(@PathVariable("teacherId") String teacherId) {
        List<CourseFilterResponse> responses = courseService.findAllCourseOfTeacher(teacherId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/course/{courseId}/basics")
    public ResponseEntity<CourseBasicsResponse> getBasicsInfo(
            Principal user,
            @PathVariable("courseId") String courseId) {
        CourseBasicsResponse response = courseService.findBasicsInfoById(user.getName(), courseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/teacher")
    public ResponseEntity<PageResponse> getCourseByTeacher(
            Principal user,
            @RequestParam(name = "kw", required = false) String kw,
            @RequestParam(name = "page", defaultValue = FilterUtils.PAGE) String page,
            @RequestParam(name = "size", defaultValue = FilterUtils.PAGE_SIZE) String size) {
        PageResponse response = courseService.findByTeacherId(user.getName(), kw, Integer.parseInt(page), Integer.parseInt(size));

        return ResponseEntity.ok(response);
    }


}
