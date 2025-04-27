package com.tp.opencourse.dto.request;


import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
    private String courseId;
    private String content;
    private Integer star;
}
