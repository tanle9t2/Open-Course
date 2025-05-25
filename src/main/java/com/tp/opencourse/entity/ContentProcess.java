package com.tp.opencourse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content_process")
public class ContentProcess {
    @Id
    private String id;
    @Column(name = "watched_time")
    private int watchedTime;
    @Column(name = "status")
    private boolean status;
    @ManyToOne
    @JoinColumn(name = "register_detail_id")
    private RegisterDetail registerDetail;

    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private Submition submition;
    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;
}
