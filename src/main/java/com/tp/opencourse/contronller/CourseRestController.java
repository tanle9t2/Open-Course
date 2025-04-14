package com.tp.opencourse.contronller;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.reponse.CourseBasicsResponse;
import com.tp.opencourse.dto.reponse.CourseFilterResponse;
import com.tp.opencourse.dto.reponse.PageResponse;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CourseService;
import com.tp.opencourse.utils.FilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:5173") // React app URL
public class CourseRestController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/course")
    public ResponseEntity<MessageResponse> createCourse(@RequestBody Map<String, String> request) {
        MessageResponse response = courseService.createCourse(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/course/{courseId}")
    public ResponseEntity<MessageResponse> updateBaisicInfo(
            @PathVariable("courseId") String id,
            @RequestParam Map<String, String> field,
            @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        MessageResponse messageResponse = courseService.updateCourse(id, field, file);
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
    public ResponseEntity<CourseBasicsResponse> getBasicsInfo(@PathVariable("courseId") String courseId) {
        CourseBasicsResponse response = courseService.findBasicsInfoById(courseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/teacher/{teacherId}")
    public ResponseEntity<PageResponse> getCourseByTeacher(
            @PathVariable("teacherId") String teacherId,
            @RequestParam(name = "kw", required = false) String kw,
            @RequestParam(name = "page", defaultValue = FilterUtils.PAGE) String page,
            @RequestParam(name = "size", defaultValue = FilterUtils.PAGE_SIZE) String size) {
        PageResponse response = courseService.findByTeacherId(teacherId, kw,Integer.parseInt(page), Integer.parseInt(size));

        return ResponseEntity.ok(response);
    }
}
