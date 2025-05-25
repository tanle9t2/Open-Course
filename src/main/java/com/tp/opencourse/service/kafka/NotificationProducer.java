package com.tp.opencourse.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.opencourse.dto.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(NotificationProducer.class); // Use LoggerFactory to initialize logger


    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Value("${kafka.topic.notification}")
    private String TOPIC;

    @Transactional
    public void sendMessage(NotificationEvent event) throws JsonProcessingException {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC, event.getEventId().toString(), event);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                // ❌ Failed
                logger.error("Failed to send message to Kafka: {}", ex.getMessage(), ex);
            } else {
                // ✅ Success
                logger.info("Message sent successfully to Kafka. Topic: {}, Partition: {}, Offset: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
