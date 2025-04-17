package com.tp.opencourse.service.kafka;

import com.tp.opencourse.dto.event.NotificationEvent;
import com.tp.opencourse.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationConsumer {
    //    private final EmailService emailService;
    @KafkaListener(
            topics = "notification-topic",
            groupId = "open-course",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTopicPaymentP1(List<NotificationEvent> notificationEvents) {
        System.out.println("consume");
        for (NotificationEvent event : notificationEvents) {

//            emailService.sendNotification(event.getUserNotification().getStudent().getEmail(), event);
        }
        System.out.println("Process event from p1 paymentEvents = " + notificationEvents);
    }
}
