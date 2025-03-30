package com.tp.opencourse.contronller;

import com.tp.opencourse.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CourseRestController {

    @Autowired
    private CourseService courseService;


}
