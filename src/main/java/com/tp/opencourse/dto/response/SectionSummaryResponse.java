package com.tp.opencourse.dto.response;

import com.tp.opencourse.dto.ContentDTO;
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
public class SectionSummaryResponse {
    private String id;
    private int totalSection;
    private int totalLecture;
    private double totalDuration;
    private List<SectionResponse> sections;
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionResponse {
        private String id;
        private String name;
        private int totalLecture;
        private double totalDuration;
        private LocalDateTime createdAt;
        private List<ContentDTO> contents;
    }
}
