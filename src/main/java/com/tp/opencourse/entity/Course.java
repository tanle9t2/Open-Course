package com.tp.opencourse.entity;

import com.tp.opencourse.entity.enums.Level;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    @Column(name = "total_duration")
    private long totalDuration;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "description")
    private String description;
    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private Level level;
    @Column(name = "banner")
    private String banner;
    @Column(name = "is_publish")
    private boolean isPublish;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    private User teacher;
    @OneToMany(mappedBy = "course")
    private List<Section> sections;

}
