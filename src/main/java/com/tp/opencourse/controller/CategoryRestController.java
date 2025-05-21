package com.tp.opencourse.controller;

import com.tp.opencourse.entity.Category;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(origins = "http://localhost:5173") // React app URL
public class CategoryRestController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<List<Category>> getCategoriesFollowLevel(@RequestParam("level") String level
            , @RequestParam("parentName") String parentName) {
        List<Category> categories = categoryService.findByParentIdAndLevel(parentName, Integer.parseInt(level));

        return ResponseEntity.ok(categories);
    }
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
