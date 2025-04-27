package com.tp.opencourse.controller;


import com.tp.opencourse.dto.response.UserProfileResponse;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CategoryService;
import com.tp.opencourse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
