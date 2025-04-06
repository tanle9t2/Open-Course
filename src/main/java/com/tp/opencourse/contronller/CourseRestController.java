package com.tp.opencourse.contronller;

import com.tp.opencourse.dto.reponse.PageResponse;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CourseService;
import com.tp.opencourse.utils.FilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:5173") // React app URL
public class CourseRestController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/course")
    private ResponseEntity<MessageResponse> createCourse(@RequestBody Map<String, String> request) {
        MessageResponse response = courseService.createCourse(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/teacher/{teacherId}")
    public ResponseEntity<PageResponse> getCourseByTeacher(
            @PathVariable("teacherId") String teacherId,
            @RequestParam(name = "page", defaultValue = FilterUtils.PAGE) String page,
            @RequestParam(name = "size", defaultValue = FilterUtils.PAGE_SIZE) String size) {
        PageResponse response = courseService.findByTeacherId(teacherId, Integer.parseInt(page), Integer.parseInt(size));

        return ResponseEntity.ok(response);
    }
}
