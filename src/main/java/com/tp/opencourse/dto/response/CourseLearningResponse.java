package com.tp.opencourse.dto.response;

import com.tp.opencourse.dto.ContentProcessDTO;
import com.tp.opencourse.dto.SectionDTO;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseLearningResponse {
    private String id;
    private String name;
    private List<SectionInfo> sections;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionInfo {
        private String id;
        private String name;
        private double totalDuration;
        private int completedLecture;
        private LocalDateTime createdAt;
        private Set<ContentProcessDTO> lectures;
    }
}
