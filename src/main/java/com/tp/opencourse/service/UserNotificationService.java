package com.tp.opencourse.service;

import com.tp.opencourse.dto.UserNotificationDTO;
import com.tp.opencourse.entity.UserNotification;
import com.tp.opencourse.response.MessageResponse;

import java.util.List;
import java.util.Map;

public interface UserNotificationService {
    List<UserNotificationDTO> findByUsername(String username);

    MessageResponse createUserNotification(Map<String, String> params);
    MessageResponse updateIsRead(String username, String id, boolean isRead);
}
