package com.tp.opencourse.controller;

import com.tp.opencourse.dto.NotificationDTO;
import com.tp.opencourse.dto.UserNotificationDTO;
import com.tp.opencourse.dto.response.UserProfileResponse;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {
    private final UserNotificationService userNotificationService;

    @MessageMapping("/notify") // Client sends here: /app/notify
    @SendTo("/topic/notifications") // Broadcast to subscribers
    public NotificationDTO sendNotification(NotificationDTO message) {
        return message;
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getUserProfile(Principal user) {

        List<UserNotificationDTO> userNotificationDTOS = userNotificationService.findByUsername(user.getName());
        return ResponseEntity.ok(userNotificationDTOS);
    }
}
