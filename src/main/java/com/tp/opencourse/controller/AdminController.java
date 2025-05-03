package com.tp.opencourse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
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
}
