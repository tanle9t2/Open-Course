package com.tp.opencourse.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.SerializationException;
import lombok.NoArgsConstructor;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class EventSerializer implements Serializer<Object> {

    private final ObjectMapper objectMapper = new ObjectMapper(

    );

    @Override
    public byte[] serialize(String s, Object o) {
        try {
            if (o == null) {
                return null;
            }
            return objectMapper.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing object", e);
        }
    }
}
