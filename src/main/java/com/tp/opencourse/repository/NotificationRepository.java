package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Notification;

import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);

    Optional<Notification> findById(String id);
}
