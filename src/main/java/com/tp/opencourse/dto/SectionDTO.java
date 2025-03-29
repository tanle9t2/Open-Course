package com.tp.opencourse.dto;

import com.tp.opencourse.entity.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionDTO {
    private String id;
    private String name;
    private CourseDTO course;
}
