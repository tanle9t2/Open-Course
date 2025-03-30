package com.tp.opencourse.dto;

import com.tp.opencourse.entity.Content;
import com.tp.opencourse.entity.RegisterDetail;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentProcessDTO {
    private String id;
    private int watchedTime;
    private boolean status;
    private ContentDTO content;
}
