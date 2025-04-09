package com.tp.opencourse.controller;


import com.tp.opencourse.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/registers")
public class RegisterController {



    @PostMapping
    public ResponseEntity<MessageResponse> registerCourses(@RequestBody Map<String, String[]> courseIds) {
        cartService.deleteCartItem(Arrays.stream(cartDetailIdList.get("idList")).toList());

        var data = courseService.findByIds(courseIdArray);
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Deleted successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
