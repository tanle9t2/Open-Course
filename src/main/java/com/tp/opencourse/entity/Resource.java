package com.tp.opencourse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "resource_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "url")
    private String url;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @OneToOne
    @JoinColumn(name = "content_id")
    @JsonIgnore
    private Content content;
}
