package com.tp.opencourse.dto.response;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LearningResponse {
    private String id; //register detail id
    private double percentComplete;
    private CourseResponse course;
}
