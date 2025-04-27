package com.tp.opencourse.dto.response;

import com.tp.opencourse.dto.CommentDTO;
import com.tp.opencourse.dto.SubmitionDTO;
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
public class SubmissionDetailResponse {
    private String id;
    private String answer;
    private LocalDateTime createdAt;
    private double mark;
    private SubmitionDTO.StudentInfo studentInfo;
    private SubmitionDTO.ContentInfo content;
    private List<CommentDTO> comments;

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class ContentInfo {
        private String id;
        private String name;
        private String url;
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
