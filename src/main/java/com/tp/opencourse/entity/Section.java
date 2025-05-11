package com.tp.opencourse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "section")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> contentList;

    public void addContent(Content content) {
        if (contentList == null)
            contentList = new ArrayList<>();
        content.setSection(this);
        contentList.add(content);
    }

    public void removeAllContent() {
        contentList.forEach(c -> this.removeContent(c));
    }

    public void removeContent(Content content) {
        content.setSection(null);
        contentList.remove(content);
    }
}
