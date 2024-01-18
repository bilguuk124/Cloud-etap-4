package com.itmo.notification.service;

import com.itmo.library.NotificationEntity;
import com.itmo.library.UserInfo;
import com.itmo.notification.bot.NotificationBot;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationBot bot;

    public void sendNotification(NotificationEntity entity) throws TelegramApiException {
        log.info("Sending notification to users");
        for (UserInfo userInfo : entity.getReceivers()){
            SendMessage sendMessage = new SendMessage(userInfo.getId().toString(), entity.getMessage());
            bot.execute(sendMessage);
            log.info("Sent notification to {}", userInfo.getUsername());
        }
    }
}
