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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO {
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private SectionDTO section;
    private Type type;
    private FileDTO file;
    private VideoDTO video;
}
