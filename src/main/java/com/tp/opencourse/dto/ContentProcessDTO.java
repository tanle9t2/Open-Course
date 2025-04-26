package com.tp.opencourse.dto;

import com.tp.opencourse.entity.Content;
import com.tp.opencourse.entity.RegisterDetail;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentProcessDTO {
    private String id;
    private int watchedTime;
    private boolean status;
    private ContentDTO content;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentProcessDTO that = (ContentProcessDTO) o;
        return Objects.equals(content.getId(), that.content.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(content.getId());
    }
}
