package com.tp.opencourse.repository;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingRepository {

    void delete(Rating rating);

    Object[] countRatingAverageAndQty(String courseId);

    List<Object[]> findRatingSummaryByCourseId(String courseId);

    void saveRating(Rating rating);

    Optional<Rating> isRatingExist(String courseId, String userId);

    Page<Rating> findRatingByCourseId(String courseId, Integer page, Integer size, Integer starCount);

    Optional<Rating> findRatingByCourseIdAndUserId(String courseId, String userId);

    Optional<Rating> findById(String courseId);

    long countRatingByCourseId(String courseId, Integer starCount);
}
