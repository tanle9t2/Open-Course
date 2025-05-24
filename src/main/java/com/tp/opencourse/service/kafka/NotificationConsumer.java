package com.tp.opencourse.service.kafka;

import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.event.NotificationEvent;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.response.MessageResponse;
import com.tp.opencourse.service.EmailService;
import com.tp.opencourse.service.UserNotificationService;
import com.tp.opencourse.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {
    private final EmailService emailService;
    private final UserService userService;
    private final UserNotificationService userNotificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "notification-topic",
                    partitions = {"0"}
            ),
            groupId = "notification-consumer1",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTopicNotificationP1(List<NotificationEvent> notificationEvents) {
        ProcessMessage(notificationEvents);
        System.out.println("Process event from P1 Notification Event = " + notificationEvents);
    }
    @KafkaListener(
            topicPartitions = @TopicPartition(
                    topic = "notification-topic",
                    partitions = {"1"}
            ),
            groupId = "notification-consumer2",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTopicNotificationP2(List<NotificationEvent> notificationEvents) {
        ProcessMessage(notificationEvents);
        System.out.println("Process event from P2 Notification Event = " + notificationEvents);
    }

    private void ProcessMessage(List<NotificationEvent> notificationEvents) {
        for (NotificationEvent event : notificationEvents) {
            String courseId = event.getNotification().getContent().get("courseId").asText();
            List<UserAuthDTO> userAuthDTOS = userService.findAllStudentInCourse(courseId);

            userAuthDTOS.forEach(s -> {
                MessageResponse response = userNotificationService.createUserNotification(
                        Map.of("studentId", s.getId(),
                                "notificationId", event.getNotification().getId())
                );

                CompletableFuture.runAsync(() -> emailService.sendNotification(s.getEmail(), event));
                messagingTemplate.convertAndSendToUser(s.getUsername(), "/notifications", response.getData());
            });
        }
    }
}
