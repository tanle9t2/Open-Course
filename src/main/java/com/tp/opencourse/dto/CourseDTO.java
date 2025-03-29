package com.tp.opencourse.dto;

import com.tp.opencourse.entity.enums.Level;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}
