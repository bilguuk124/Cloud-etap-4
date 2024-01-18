package com.itmo.telegram.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {


    private final KafkaProducer<String, String > kafkaTemplate;
    private final ObjectMapper objectMapper = new MappingJackson2MessageConverter().getObjectMapper();

    @Value("${kafka.topic}")
    private String topicName;

    public void sendMessage(Object payload)  {
        try {
            String message = null;
            message = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(new ProducerRecord<>(topicName, "key", message));
            kafkaTemplate.flush();
        }
        catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }


}
