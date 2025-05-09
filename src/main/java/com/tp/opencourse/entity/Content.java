package com.tp.opencourse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tp.opencourse.entity.enums.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;
    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private Resource resource;

    @ManyToOne
    @JoinColumn(name = "main_content_id")
    private Content mainContent;


    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentProcess> contentProcesses;


    // ✅ Parent to child (this is the inverse side)
    @OneToMany(mappedBy = "mainContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> subContents;

    public void addSubContent(Content subContent) {
        if (subContents == null)
            subContents = new ArrayList<>();

        subContent.setMainContent(this);
        subContents.add(subContent);
    }

    public void changeMainResource(Resource resource) {
        if (this.resource != null)
            this.resource.setContent(null);
        resource.setContent(this);
        this.resource = resource;
    }
}
