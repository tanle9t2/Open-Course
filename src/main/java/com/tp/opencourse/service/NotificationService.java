package com.tp.opencourse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tp.opencourse.entity.User;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    void createNotification(User user, Map<String, String> data) throws JsonProcessingException;

    void createNotificationInCourse(String username, String courseId, String content) throws JsonProcessingException;

}
