package com.tp.opencourse.service;

import com.tp.opencourse.dto.request.RatingRequest;
import com.tp.opencourse.dto.response.PageResponse;
import com.tp.opencourse.dto.response.RatingResponse;
import com.tp.opencourse.dto.response.RatingSummaryResponse;

public interface RatingService {
    void rateCourse(RatingRequest ratingRequest);
    boolean isRateCourse(RatingRequest ratingRequest);
    RatingSummaryResponse findRatingSummary(String courseId);
    PageResponse<RatingResponse> findRatingsByCourseId(String courseId, String page, String size, Integer starCount);
    RatingResponse findRatingByCourseIdAndUsername(String courseId);
}
