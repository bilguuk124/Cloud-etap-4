package com.itmo.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.library.NotificationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService service;
    private final ObjectMapper objectMapper = new MappingJackson2MessageConverter().getObjectMapper();

    @KafkaListener(topics = "notification")
    public void consume(String payload)  {
            try{
                log.info("Consumed {} from queue", payload);
                NotificationEntity notification = objectMapper.readValue(payload, NotificationEntity.class);
                service.sendNotification(notification);
            } catch (TelegramApiException | JsonProcessingException e){
                log.error(e.getMessage());
            }
    }
}
