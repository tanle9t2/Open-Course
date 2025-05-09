package com.tp.opencourse.controller;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.dto.response.PageResponseT;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.service.CourseService;
import com.tp.opencourse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("message", "Hello Thymeleaf!");
        return "dashboard"; // Renders /WEB-INF/templates/home.html
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("message", "Hello Thymeleaf!");
        return "dashboard"; // Renders /WEB-INF/templates/home.html
    }

    @GetMapping("/form-elements")
    public String formElements(Model model) {
        model.addAttribute("message", "Hello Thymeleaf!");
        return "form-elements"; // Renders /WEB-INF/templates/home.html
    }

    @GetMapping("/table-elements")
    public String tableElements(Model model) {
        model.addAttribute("message", "Hello Thymeleaf!");
        return "table-elements"; // Renders /WEB-INF/templates/home.html
    }

    @GetMapping("/course-detail/{courseId}")
    public String courseDetail(@PathVariable("courseId") String courseId, Model model) {
        CourseDTO courseDTO = courseService.findById(courseId);
        model.addAttribute("sections", courseDTO.getSections());
        model.addAttribute("course", courseDTO);
        return "course-detail"; // Renders /WEB-INF/templates/home.html
    }

    @GetMapping("/course-overview")
    public String courseOverview(Model model,
                                 @RequestParam(defaultValue = "1", required = false) String page,
                                 @RequestParam(defaultValue = "3", required = false) String size,
                                 @RequestParam(name = "sortBy", required = false) String sortBy,
                                 @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction,
                                 @RequestParam(name = "keyword", required = false) String keyword) {
        PageResponseT course = courseService.findAllBasicsInfo(keyword, Integer.parseInt(page),
                Integer.parseInt(size), sortBy, direction);
        model.addAttribute("courses", course.getData());
        model.addAttribute("currentPage", course.getPage());
        model.addAttribute("totalPages", course.getTotalPages());
        return "course-overview"; // Renders /WEB-INF/templates/home.html
    }

    @GetMapping("/accept-course")
    public String getCourseAccept(Model model,
                                  @RequestParam(defaultValue = "1", required = false) String page,
                                  @RequestParam(defaultValue = "3", required = false) String size,
                                  @RequestParam(name = "sortBy", required = false) String sortBy,
                                  @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction,
                                  @RequestParam(name = "keyword", required = false) String keyword) {
        PageResponseT course = courseService.findAllByActive(keyword, Integer.parseInt(page), Integer.parseInt(size),
                sortBy, direction);

        model.addAttribute("courses", course.getData());
        model.addAttribute("currentPage", course.getPage());
        model.addAttribute("totalPages", course.getTotalPages());
        return "accept-course"; // Renders /WEB-INF/templates/home.html
    }

    @GetMapping("/teachers")
    public String getTeachers(Model model,
                              @RequestParam(defaultValue = "1", required = false) String page,
                              @RequestParam(defaultValue = "3", required = false) String size,
                              @RequestParam(name = "keyword", required = false) String keyword) {
        PageResponseT course = userService.getAllProfileTeacher(keyword, Integer.parseInt(page), Integer.parseInt(size));

        model.addAttribute("teachers", course.getData());
        model.addAttribute("currentPage", course.getPage());
        model.addAttribute("totalPages", course.getTotalPages());
        return "teacher-overview"; // Renders /WEB-INF/templates/home.html
    }
}
