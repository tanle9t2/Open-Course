package com.tp.opencourse.dto;

import com.tp.opencourse.entity.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionDTO {
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private List<ContentDTO> contentList;
}
