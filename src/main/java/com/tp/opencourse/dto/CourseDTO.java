package com.tp.opencourse.dto;

import com.tp.opencourse.entity.Section;
import com.tp.opencourse.entity.enums.CourseStatus;
import com.tp.opencourse.entity.enums.Level;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private String id;
    private String name;
    private double price;
    private long totalDuration;
    private LocalDateTime createdAt;
    private String description;
    private Level level;
    private String banner;
    private String status;
    private TeacherInfo teacherInfo;
    private List<SectionDTO> sections;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherInfo {
        private String id;
        private String name;
    }

}
