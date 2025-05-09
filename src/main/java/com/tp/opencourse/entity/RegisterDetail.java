package com.tp.opencourse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "register_detail")
public class RegisterDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "percent_complete")
    private double percentComplete;

    @Column(name = "price")
    private double price;

    @ManyToOne
    @JoinColumn(name = "register_id")
    private Register register;

    @OneToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "registerDetail",cascade = CascadeType.MERGE)
    private List<ContentProcess> contentProcesses;

    @OneToOne(mappedBy = "registerDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Rating rating;

    @OneToOne(mappedBy = "registerDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Certification certification;

    public void addContentProcess(ContentProcess contentProcess) {
        if (contentProcesses == null)
            contentProcesses = new ArrayList<>();
        contentProcess.setRegisterDetail(this);
        contentProcesses.add(contentProcess);
    }

    public void addRating(Rating rating) {
        this.rating = rating;
    }
}
