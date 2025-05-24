package com.tp.opencourse.controller;

import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.RatingRepository;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.ContentService;
import com.tp.opencourse.service.RatingService;
import com.tp.opencourse.service.impl.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
public class TestController {
    @Autowired
    private ContentService contentService;
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    TestService testService;
    @Autowired
    RatingService ratingService;
    @PostMapping("/test")
    public ResponseEntity<MessageResponse> createVideo(@RequestParam Map<String, String> field,
                                                       @RequestParam("file") MultipartFile file) throws IOException {
//        contentService.createContent(field, file);
        return ResponseEntity.ok(MessageResponse.builder()
                .data(null)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/init")
    public ResponseEntity<MessageResponse> init() {
        testService.createIndex();
        return ResponseEntity.ok(MessageResponse.builder()
                .data(null)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/rating/summary")
    public ResponseEntity<MessageResponse> getRatingSummary() {
        return ResponseEntity.ok(MessageResponse.builder()
                .data(null)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/rating")
    public ResponseEntity<MessageResponse> getRAting() throws IOException {
        Object[] x = ratingRepository.countRatingAverageAndQty("1");
        return ResponseEntity.ok(MessageResponse.builder()
                .data(x)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/count")
    public ResponseEntity<MessageResponse> getTotalLecture() throws IOException {
        long x = courseRepository.countTotalLecture("1");
        return ResponseEntity.ok(MessageResponse.builder()
                .data(x)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/secure")
    public ResponseEntity<MessageResponse> secure() throws IOException {
        return ResponseEntity.ok(MessageResponse.builder()
                .data("Xin chao")
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }


}
