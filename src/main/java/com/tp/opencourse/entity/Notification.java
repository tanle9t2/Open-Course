package com.tp.opencourse.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.tp.opencourse.converter.JsonNodeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "content",columnDefinition = "json")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode content;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;
}
