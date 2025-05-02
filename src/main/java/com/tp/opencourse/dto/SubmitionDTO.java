package com.tp.opencourse.dto;

import com.tp.opencourse.entity.Content;
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
public class SubmitionDTO {
    private String id;
    private String answer;
    private LocalDateTime createdAt;
    private Double mark;
    private StudentInfo studentInfo;
    private ContentInfo content;
    private List<CommentDTO> comments;

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class ContentInfo {
        private String id;
        private String name;
        private String resourceUrl;
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class StudentInfo {
        private String id;
        private String name;
        private String avt;
    }
}
