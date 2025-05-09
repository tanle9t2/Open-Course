package com.tp.opencourse.mapper.deserialization;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

public class NullSafeJsonDeserializer extends JsonDeserializer<JsonNode> {

    public NullSafeJsonDeserializer() {
        super(JsonNode.class);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        super.configure(configs, isKey);
    }

    @Override
    public JsonNode deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            System.out.println("Received null or empty message for topic: " + topic);
            return null;
        }
        try {
            return super.deserialize(topic, data);
        } catch (Exception e) {
            System.err.println("Failed to deserialize message for topic: " + topic + ". Error: " + e.getMessage());
            throw new SerializationException("Error deserializing JSON message from topic: " + topic, e);
        }
    }

    @Override
    public void close() {
        super.close();
    }
}