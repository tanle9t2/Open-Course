package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Rating;

import java.util.List;

public interface RatingRepository {
    Object[] countRatingAverageAndQty(String courseId);
    void saveRating(Rating rating);
    long isRatingExist(String courseId, String userId);
}
