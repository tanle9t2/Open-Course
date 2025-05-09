package com.tp.opencourse.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRevenueResponse {
    private String id;
    private String username;
    private String fullName;
    private LocalDateTime createdAt;
    private String avt;
    private double totalRevenue;
}
