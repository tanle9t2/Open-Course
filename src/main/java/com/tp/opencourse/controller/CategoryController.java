package com.tp.opencourse.controller;


import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/root")
    public ResponseEntity<MessageResponse> getRootCategory() {
        var data = categoryService.getRootCategory();
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/nested/{parentId}")
    public ResponseEntity<MessageResponse> getNestedCategory(@PathVariable("parentId") String parentId) {
        var data = categoryService.getNestedCategory(parentId);
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }



}
