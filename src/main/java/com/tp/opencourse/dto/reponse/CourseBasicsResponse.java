package com.tp.opencourse.dto.reponse;

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
