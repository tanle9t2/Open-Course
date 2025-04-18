package com.tp.opencourse.service.kafka;

import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.event.NotificationEvent;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.EmailService;
import com.tp.opencourse.service.UserNotificationService;
import com.tp.opencourse.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {
    private final EmailService emailService;
    private final UserService userService;
    private final UserNotificationService userNotificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "notification-topic",
            groupId = "open-course",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTopicPaymentP1(List<NotificationEvent> notificationEvents) {
        System.out.println("consume");
        for (NotificationEvent event : notificationEvents) {
            String courseId = event.getNotification().getContent().get("courseId").asText();
            List<UserAuthDTO> userAuthDTOS = userService.findAllStudentInCourse(courseId);

            userAuthDTOS.forEach(s -> userNotificationService.createUserNotification(
                    Map.of("studentId", s.getId(),
                            "notificationId", event.getNotification().getId())
            ));
//            userAuthDTOS.forEach(e -> emailService.sendNotification(e.getEmail(), event));
            messagingTemplate.convertAndSend("/topic/notifications", event);

        }
        System.out.println("Process event from p1 paymentEvents = " + notificationEvents);
    }
}
