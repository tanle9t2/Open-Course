package com.tp.opencourse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "file")
@DiscriminatorValue("FILE")
public class File extends Resource {
    // No additional fields
}
