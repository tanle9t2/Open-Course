package com.tp.opencourse.controller;


import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.response.CourseLearningResponse;
import com.tp.opencourse.dto.response.UserProfileResponse;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CategoryService;
import com.tp.opencourse.service.CourseService;
import com.tp.opencourse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CourseService courseService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        UserProfileResponse userProfileDTO = userService.getProfile();
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("ok")
                .data(userProfileDTO)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseLearningResponse> getCourse(Principal user, @PathVariable("courseId") String courseId) {
        CourseLearningResponse courseLearning = courseService.findCourseLearning(user.getName(), courseId);
        return ResponseEntity.ok(courseLearning);
    }


    @PostMapping(value = "/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserProfile(
            @RequestParam Map<String, String> fields,
            @RequestParam(name = "image", required = false) MultipartFile image
    ) {
        userService.updateProfile(fields, image);
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("ok")
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
