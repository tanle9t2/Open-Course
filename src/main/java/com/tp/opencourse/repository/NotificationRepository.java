package com.tp.opencourse.repository;

import com.thoughtworks.qdox.model.expression.Not;
import com.tp.opencourse.entity.Notification;

public interface NotificationRepository {
    Notification create(Notification notification);
}
