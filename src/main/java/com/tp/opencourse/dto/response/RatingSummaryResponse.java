package com.tp.opencourse.dto.response;


import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingSummaryResponse {
    private Integer totalRating;
    private Double averageRating;
    private List<RatingSummaryItem> ratingSummaryItems;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingSummaryItem {
        private Integer rating;
        private Integer percentage;
    }
}
