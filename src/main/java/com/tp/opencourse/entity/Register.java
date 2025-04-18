package com.tp.opencourse.entity;

import com.tp.opencourse.entity.enums.RegisterStatus;
import com.tp.opencourse.entity.enums.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "register")
public class Register {
    @Id
    private String id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RegisterStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @OneToMany(mappedBy = "register", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RegisterDetail> registerDetails;

    @OneToMany(mappedBy = "register", fetch = FetchType.LAZY)
    private List<Payment> payments;

    public void addPayment(Payment payment) {
        if(payments == null) {
            payments = new ArrayList<>();
        }
        payments.add(payment);
    }
    @PrePersist
    public void generateId() {
        if (id == null) {
            this.id = UUID.randomUUID().toString().replace("-", "");
        }
    }
}
