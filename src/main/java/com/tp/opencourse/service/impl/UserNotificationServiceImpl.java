package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.UserNotificationDTO;
import com.tp.opencourse.entity.Notification;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.entity.UserNotification;
import com.tp.opencourse.exceptions.AccessDeniedException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.UserNotificationMapper;
import com.tp.opencourse.repository.NotificationRepository;
import com.tp.opencourse.repository.UserNotificationRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.repository.impl.UserNotificationRepositoryImpl;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationMapper userNotificationMapper;

    @Override
    @Transactional
    public List<UserNotificationDTO> findByUsername(String username) {
        List<UserNotificationDTO> userNotificationDTOS = userNotificationRepository.findByUserId(username)
                .stream()
                .map(n -> userNotificationMapper.convertDTO(n))
                .collect(Collectors.toList());

        return userNotificationDTOS;
    }

    @Override
    @Transactional
    public MessageResponse createUserNotification(Map<String, String> params) {
        User student = userRepository.findById(params.get("studentId"))
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found user"));
        Notification notification = notificationRepository.findById(params.get("notificationId"))
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found notification"));

        UserNotification userNotification = UserNotification.builder()
                .student(student)
                .notification(notification)
                .isRead(false)
                .build();
        userNotificationRepository.save(userNotification);

        return MessageResponse.builder()
                .message("Successfully create user notification")
                .data(userNotificationMapper.convertDTO(userNotification))
                .status(HttpStatus.CREATED)
                .build();
    }

    @Override
    @Transactional
    public MessageResponse updateIsRead(String username, String id, boolean isRead) {
        UserNotification userNotification = userNotificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExeption("Not found notification"));
        if (!userNotification.getStudent().getUsername().equals(username))
            throw new AccessDeniedException("You don't have permission for this resources");

        userNotification.setRead(isRead);
        userNotification = userNotificationRepository.save(userNotification);
        return MessageResponse.builder()
                .data(userNotificationMapper.convertDTO(userNotification))
                .status(HttpStatus.OK)
                .message("Successfully updated read")
                .build();
    }
}
