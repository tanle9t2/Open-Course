package com.tp.opencourse.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tp.opencourse.dto.UserNotificationDTO;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.NotificationService;
import com.tp.opencourse.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationRestController {
    private final UserNotificationService userNotificationService;
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<?> getUserNotification(Principal user) {

        List<UserNotificationDTO> userNotificationDTOS = userNotificationService.findByUsername(user.getName());
        return ResponseEntity.ok(userNotificationDTOS);
    }

    @PostMapping("/notifications")
    public ResponseEntity<?> creatNotificationInCourse(Principal user,
                                                       @RequestBody Map<String, String> params
    ) throws JsonProcessingException {
        String content = params.get("content");
        String courseId = params.get("courseId");
        notificationService.createNotificationInCourse(user.getName(), courseId, content);
        return ResponseEntity.ok("OK");
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
