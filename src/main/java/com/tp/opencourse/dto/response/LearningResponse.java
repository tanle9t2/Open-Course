package com.tp.opencourse.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LearningResponse {
    private String id; //register detail id
    private double percentComplete;
    private boolean isCompleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime purchasedAt;  //registered day
    private String certificationId;
    private CourseResponse course;
    private RatingResponse rating;
}
