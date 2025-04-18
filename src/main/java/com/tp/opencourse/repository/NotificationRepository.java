package com.tp.opencourse.repository;

import com.thoughtworks.qdox.model.expression.Not;
import com.tp.opencourse.entity.Notification;

import java.util.Optional;

public interface NotificationRepository {
    Notification create(Notification notification);

    Optional<Notification> findById(String id);
}
