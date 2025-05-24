package com.tp.opencourse.controller;


import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/registers")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerCourses(@RequestBody Map<String, String[]> courseIds) {
        Map<String, String> responses = registerService.registerCourses(Arrays.stream(courseIds.get("courseIds")).toList());
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Registered successfully")
                .data(responses)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public ResponseEntity<MessageResponse> cancelRegisterCourses(@RequestBody Map<String, String> registerId) {
        registerService.cancelRegister(registerId.get("registerId"));
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Registered successfully")
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/purchase")
    public ResponseEntity<MessageResponse> getPurchase(@RequestParam("status") String status) {
        var data = registerService.findAllRegisteredCourses(status);
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Deleted successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
