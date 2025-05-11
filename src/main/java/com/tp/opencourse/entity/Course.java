package com.tp.opencourse.entity;

import com.tp.opencourse.entity.enums.CourseStatus;
import com.tp.opencourse.entity.enums.Level;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "price")
    private double price;

    @Column(name = "banner")
    private String banner;

    @Column(name = "is_publish")
    private boolean isPublish;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category categories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Section> sections;


    public void addSection(Section section) {
        section.setCourse(this);
        sections.add(section);
    }

    public double getTotalDuration() {
        if (sections == null) return 0;
        return sections.stream()
                .flatMap(s -> s.getContentList().stream())
                .mapToDouble(c -> c.getResource() instanceof Video ? ((Video) c.getResource()).getDuration() : 60)
                .sum();
    }

}
