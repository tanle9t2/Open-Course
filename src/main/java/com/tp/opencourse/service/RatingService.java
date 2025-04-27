package com.tp.opencourse.service;

import com.tp.opencourse.dto.request.RatingRequest;

public interface RatingService {
    void rateCourse(RatingRequest ratingRequest);
    boolean isRateCourse(RatingRequest ratingRequest);
}
