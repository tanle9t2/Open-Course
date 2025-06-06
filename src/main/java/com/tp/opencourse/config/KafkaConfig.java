package com.tp.opencourse.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.tp.opencourse.converter.EventSerializer;
import com.tp.opencourse.dto.event.NotificationEvent;
import com.tp.opencourse.mapper.deserialization.NullSafeJsonDeserializer;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${kafka.boostrap-server}")
    private String BOOTSTRAP_SERVERS;

    @Value("${kafka.group-id}")
    private String GROUP_ID;

    @Value("${kafka.topic.notification}")
    private String TOPIC_NOTIFICATION;
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        return new KafkaAdmin(configs);
    }

    // ====== Producer ======
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);  // Ensure this is correctly set
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class);  // Use the custom EventSerializer
        return new DefaultKafkaProducerFactory<>(configProps);
    }


    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, NotificationEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
//
        JsonDeserializer<NotificationEvent> deserializer = new JsonDeserializer<>(NotificationEvent.class);
        deserializer.addTrustedPackages("com.tp.opencourse.dto.event");

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true); // Since you consume List<NotificationEvent>
        return factory;
    }

    @Bean
    public ConsumerFactory<String, JsonNode> consumerSyncFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                new NullSafeJsonDeserializer()
        );
    }

    @Bean(name = "kafkaListenerSyncContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, JsonNode> kafkaListenerSyncContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, JsonNode> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerSyncFactory());
        factory.setBatchListener(false); // Since you consume List<NotificationEvent>
        return factory;
    }

    @Bean
    public NewTopic buildTopic() {
        return TopicBuilder.name(TOPIC_NOTIFICATION)
                .partitions(2)
                .replicas(2)
                .build();
    }
}
