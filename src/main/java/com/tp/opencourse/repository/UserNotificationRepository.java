package com.tp.opencourse.repository;


import com.tp.opencourse.entity.User;
import com.tp.opencourse.entity.UserNotification;

import java.util.List;

public interface UserNotificationRepository {
    UserNotification create(UserNotification userNotification);

    List<UserNotification> findByUserId(String userId);
}
