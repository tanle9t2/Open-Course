package com.tp.opencourse.controller;


import com.tp.opencourse.dto.request.RatingRequest;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ratings")
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


}
