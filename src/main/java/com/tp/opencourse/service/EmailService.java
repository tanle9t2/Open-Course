package com.tp.opencourse.service;

import com.tp.opencourse.dto.event.NotificationEvent;

public interface EmailService {
    void sendNotification(String toEmail, NotificationEvent notificationEvent);
}
