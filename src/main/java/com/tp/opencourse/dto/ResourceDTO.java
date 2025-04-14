package com.tp.opencourse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class ResourceDTO {
    private String id;
    private String url;
    private String name;
    private LocalDateTime createdAt;
    private String type;
}
