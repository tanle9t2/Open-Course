package com.tp.opencourse.entity;

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
@Table(name = "submition")
public class Submition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "answer")
    private String answer;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @OneToOne
    @JoinColumn(name = "content_process_id")
    private ContentProcess content;
    @Column(name = "mark")
    private Double mark;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "submition")
    private List<Comment> comments;

    public void addComment(Comment comment) {
        if (comments == null)
            comments = new ArrayList<>();
        comment.setSubmition(this);
        comments.add((comment));
    }

}
