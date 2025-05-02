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
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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

    @PutMapping("/notification/{notificationId}")
    public ResponseEntity<?> updateNotification(Principal user
            , @PathVariable("notificationId") String notificationId
            , @RequestBody Map<String, String> request) {

        boolean isRead = Boolean.parseBoolean(request.get("isRead"));
        MessageResponse messageResponse = userNotificationService.updateIsRead(user.getName(), notificationId, isRead);
        return ResponseEntity.ok(messageResponse);
    }
}
