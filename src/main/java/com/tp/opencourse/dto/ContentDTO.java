package com.tp.opencourse.dto;

import com.tp.opencourse.entity.File;
import com.tp.opencourse.entity.Section;
import com.tp.opencourse.entity.Video;
import com.tp.opencourse.entity.enums.Type;
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
public class ContentDTO {
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private Type type;
    private FileDTO file;
    private VideoDTO video;
    private List<SubContent> subContents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubContent {
        private String id;
        private String name;
        private LocalDateTime createdAt;
        private Type type;
        private FileDTO file;
        private VideoDTO video;
    }
}
