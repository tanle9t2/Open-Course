package com.tp.opencourse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "video")
@DiscriminatorValue("VIDEO")
public class Video extends Resource {
    @Column(name = "duration")
    private Double duration;
}
