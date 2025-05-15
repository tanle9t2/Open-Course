package com.tp.opencourse.dto.response;

import com.tp.opencourse.entity.enums.CourseStatus;
import com.tp.opencourse.entity.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CourseBasicsResponse {
    private String id;
    private String name;
    private String description;
    private String level;
    private List<Level> levels;
    private CategoryBasic category;
    private double totalDuration;
    private CourseStatus status;
    private String banner;
    private double price;
    private boolean isPublish;


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class CategoryBasic {
        private String id;
        private String name;
    }

}
