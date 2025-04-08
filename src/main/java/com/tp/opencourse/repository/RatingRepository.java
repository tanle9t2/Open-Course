package com.tp.opencourse.repository;

import java.util.List;

public interface RatingRepository {
    Object[] countRatingAverageAndQty(String courseId);
}
