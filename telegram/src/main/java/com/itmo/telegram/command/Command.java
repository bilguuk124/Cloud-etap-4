package com.itmo.telegram.command;

import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface Command {
    String getDescription();
    String getName();
    void handleCommand(MessageContext ctx, SilentSender sender) ;
}
