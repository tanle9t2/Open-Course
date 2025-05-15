package com.tp.opencourse.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.tp.opencourse.entity.enums.CourseStatus;
import com.tp.opencourse.entity.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.jmx.export.annotation.ManagedNotifications;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CourseResponse {
    private String id;
    private String name;
    private String description;
    private double price;
    private double totalDuration;
    private long totalLecture;
    private long totalRegistration;
    private boolean isPublish;
    private CourseStatus status;
    private Level level;
    private String banner;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private CategoryResponse categoryInfo;
    private TeacherInfo teacherInfo;
    private RatingInfo ratingInfo;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherInfo {
        private String id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingInfo {
        private double average;
        private long qty;
    }
}
