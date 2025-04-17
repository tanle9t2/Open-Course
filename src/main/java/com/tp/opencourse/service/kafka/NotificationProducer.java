package com.tp.opencourse.service.kafka;

import com.tp.opencourse.dto.event.NotificationEvent;
import com.tp.opencourse.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationProducer {


    private static final Logger logger = LoggerFactory.getLogger(NotificationProducer.class); // SLF4J Logger

    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Value("${kafka.topic.notification}")
    private String TOPIC;

    @Transactional
    public void sendMessage(NotificationEvent event) {
        // Log the message being sent
        logger.info("Sending message to topic {}: {}", TOPIC, event);
        ProducerRecord<String, Object> record = new ProducerRecord<>(TOPIC, event);
        // Send the message to Kafka and use a callback to log the result
        kafkaTemplate.send(record);
    }
}
