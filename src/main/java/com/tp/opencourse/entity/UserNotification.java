package com.tp.opencourse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_notification")
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "is_read")
    private boolean isRead;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;
    @ManyToOne
    @JoinColumn(name = "notification_id")
    private Notification notification;

}
