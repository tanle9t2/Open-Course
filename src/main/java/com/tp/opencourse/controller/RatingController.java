package com.tp.opencourse.controller;


import com.tp.opencourse.dto.request.RatingRequest;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/create-rating")
    public ResponseEntity<MessageResponse> createRating(@RequestBody RatingRequest ratingRequest) {
        ratingService.rateCourse(ratingRequest);
        MessageResponse apiResponse = MessageResponse.builder()
                .status(HttpStatus.OK)
                .message("Deleted successfully")
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/courses/{courseId}/rating/summary")
    public ResponseEntity<MessageResponse> getRatingSummary(@PathVariable("courseId") String courseId) {
        var x = ratingService.findRatingSummary(courseId);
        return ResponseEntity.ok(MessageResponse.builder()
                .data(x)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/courses/{courseId}/rating")
    public ResponseEntity<MessageResponse> getRating(@PathVariable("courseId") String courseId,
                                                     @RequestParam(value = "page", defaultValue = "1") String page,
                                                     @RequestParam(value = "size", defaultValue = "3") String size,
                                                     @RequestParam(value = "starCount", required = false) Integer starCount) {
        var data = ratingService.findRatingsByCourseId(courseId, page, size, starCount);
        return ResponseEntity.ok(MessageResponse.builder()
                .data(data)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/courses/{courseId}/rating/user")
    public ResponseEntity<MessageResponse> getUserRating(@PathVariable("courseId") String courseId) {
        var data = ratingService.findRatingByCourseIdAndUsername(courseId);
        return ResponseEntity.ok(MessageResponse.builder()
                .data(data)
                .message("Successfully created content")
                .status(HttpStatus.OK)
                .build());
    }
}
