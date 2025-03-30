package com.tp.opencourse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content_process")
public class ContentProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "watched_time")
    private int watchedTime;
    @Column(name = "status")
    private boolean status;
    @ManyToOne
    @JoinColumn(name = "register_detail_id")
    private RegisterDetail registerDetail;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;
}
