package com.tp.opencourse.repository;


import com.tp.opencourse.entity.User;
import com.tp.opencourse.entity.UserNotification;

import java.util.List;
import java.util.Optional;

public interface UserNotificationRepository {
    UserNotification save(UserNotification userNotification);

    List<UserNotification> findByUserId(String userId);
    Optional<UserNotification> findById(String id);
}
