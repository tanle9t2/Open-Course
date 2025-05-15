package com.tp.opencourse.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tp.opencourse.entity.enums.CourseStatus;
import com.tp.opencourse.entity.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CourseAdminResponse {
    private String name;
    private double price;
    private boolean isPublish;
    private CourseStatus status;

    private String teacherName;
    private double diff;

}
