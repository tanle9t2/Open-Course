package com.tp.opencourse.dto.response;

import com.tp.opencourse.dto.reponse.CourseBasicsResponse;
import lombok.*;

import java.util.List;

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
