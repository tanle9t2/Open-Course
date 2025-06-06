package com.tp.opencourse.controller;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.request.AdminLoginRequest;
import com.tp.opencourse.dto.request.UserAdminRegister;
import com.tp.opencourse.dto.response.*;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.dto.response.PageResponseT;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CourseService;
import com.tp.opencourse.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.tp.opencourse.dto.request.UserAdminRequest;
import com.tp.opencourse.dto.response.PageResponseT;
import com.tp.opencourse.entity.Role;
import com.tp.opencourse.service.AuthService;
import com.tp.opencourse.service.StatService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final CourseService courseService;
    private final UserService userService;

    private final AuthService authService;
    private final StatService statService;
    private final AuthenticationManager authenticationManager;
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute(value = "user") AdminLoginRequest user, Model model, HttpSession session) {
        try {
            Authentication authentication = authService.mvcLogin(user);

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        } catch (Exception exception) {
            model.addAttribute("error", exception.getMessage());
            return "login";
        }
        return "redirect:home";
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String dashboard(Model model) {
        model.addAttribute("message", "Hello Thymeleaf!");
        return "dashboard"; // Renders /WEB-INF/templates/home.html
    }


    @GetMapping("/home")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String home(Model model,
                       @RequestParam(value = "periodType", required = false, defaultValue = "") String periodType,
                       @RequestParam(value = "year", required = false, defaultValue = "") String year
                       ) {

        Object[] statsOverview = statService.getOverview();
        List<PeriodStatisticResponse> periodStatisticResponses = statService.getStatisticsByPeriod(year, periodType);
        List<CourseAdminResponse> courseAdminResponses = statService.getCourseStatistic("1", "5");

        Long totalCourse = (Long) statsOverview[0];
        Long totalUser = (Long) statsOverview[1];
        Long totalRegistration = (Long) statsOverview[2];
        Double totalRevenue = (Double) statsOverview[3];

        List<Integer> years = IntStream.rangeClosed(2010, LocalDate.now().getYear())
                .boxed()
                .sorted(Comparator.reverseOrder())
                .toList();
        List<String> periodTypes = List.of("Month", "Quarter");

        model.addAttribute("totalCourse", totalCourse);
        model.addAttribute("totalUser", totalUser);
        model.addAttribute("totalRegistration", totalRegistration);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("periodTypes", periodTypes);
        model.addAttribute("years", years);
        model.addAttribute("statistic", periodStatisticResponses);
        model.addAttribute("courses", courseAdminResponses);
        model.addAttribute("selectedPeriodType", periodType);
        model.addAttribute("selectedYear", year);

        return "dashboard";
    }

    @PostMapping("/users/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addUser(@ModelAttribute(value = "user") @Valid UserAdminRegister userRequest,
                BindingResult result,
                RedirectAttributes redirectAttributes) throws IOException {
        try {
            if(result.hasErrors()) {
                return "user-register";
            }
            authService.register(userRequest);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return String.format("redirect:/users/register");
        }
        return "redirect:/users";
    }

    @GetMapping("/users/register")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String userRegister(@ModelAttribute(value = "user") UserAdminRegister userRequest) {
        return "user-register";
    }

    @PostMapping("/users/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String updateUser(@ModelAttribute(value = "user") @Valid UserAdminRequest userRequest,
            BindingResult result,
            @RequestParam(required = false, name = "avatarFile") MultipartFile avatarFile,
            @RequestParam(required = false, name = "roles") List<String> roleNames,
            RedirectAttributes redirectAttributes) throws IOException {
        try {
            if(result.hasErrors()) {
                return "user-detail";
            }
            userService.updateUser(userRequest, roleNames, avatarFile);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return String.format("redirect:/users/detail/%s", userRequest.getId());
        }
        return "redirect:/users";
    }


    @GetMapping("/users/detail/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String userDetail(Model model, @PathVariable("id") String id) {
        UserAdminResponse user = userService.findById(id);
        List<Role> roles = authService.getRoles();

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "user-detail";
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String userList(Model model,
                           @RequestParam(name = "page", defaultValue = "1", required = false) String page,
                           @RequestParam(name = "size", defaultValue = "3", required = false) String size,
                           @RequestParam(name = "sortBy", required = false) String sortBy,
                           @RequestParam(name = "direction", required = false, defaultValue = "DESC") String direction,
                           @RequestParam(name = "keyword", required = false) String keyword) {
        PageResponse<UserAdminResponse> users = userService.findAll(keyword, Integer.parseInt(page),
                Integer.parseInt(size), sortBy, direction);
        model.addAttribute("users", users.getContent());
        model.addAttribute("currentPage", users.getPage());
        model.addAttribute("totalPages", users.getTotalPages());
        return "users";
    }


    @GetMapping("/course-detail/{courseId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String courseDetail(@PathVariable("courseId") String courseId, Model model) {
        CourseDTO courseDTO = courseService.findById(courseId);
        model.addAttribute("sections", courseDTO.getSections());
        model.addAttribute("course", courseDTO);
        return "course-detail"; // Renders /WEB-INF/templates/home.html
    }

    @GetMapping("/course-overview")
    @PreAuthorize("hasAuthority('ADMIN')")
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
        model.addAttribute("keyword", keyword);
        model.addAttribute("direction", direction);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("totalPages", course.getTotalPages());
        return "course-overview"; // Renders /WEB-INF/templates/home.html
    }

    @PutMapping("/course/{courseId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MessageResponse> updateStatusCourse(
            @PathVariable String courseId,
            @RequestBody Map<String, String> request) {
        MessageResponse response = courseService.updateStatus(courseId, request.get("status"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accept-course")
    @PreAuthorize("hasAuthority('ADMIN')")
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
        model.addAttribute("keyword", keyword);
        model.addAttribute("direction", direction);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("totalPages", course.getTotalPages());
        return "accept-course"; // Renders /WEB-INF/templates/home.html
    }

    @GetMapping("/teachers")
    @PreAuthorize("hasAuthority('ADMIN')")
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
