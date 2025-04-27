package com.tp.opencourse.controller;

import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CourseService;
import com.tp.opencourse.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/learnings")
public class LearningController {

    @Autowired
    private RegisterService registerService;

    @GetMapping("/my-learning")
    public ResponseEntity<MessageResponse> getLearning() {
        var data = registerService.findAllLearnings();
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("get successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
