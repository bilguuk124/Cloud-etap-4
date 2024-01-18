package com.itmo.telegram.command;

import com.itmo.telegram.exception.WrongArgumentAmountException;
import com.itmo.telegram.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class StartCommand extends AbstractCommand{

    private final UserService userService;

    public StartCommand(UserService userService) {
        super("/start", "Usage: (/start)");
        this.userService = userService;
    }


    @Override
    public void handleCommand(MessageContext ctx, SilentSender sender) {
        try{
            String[] args = ctx.update().getMessage().getText().split(" ");
            if (args.length != 1) throw new WrongArgumentAmountException();
            User user = ctx.user();
            userService.registerUser(user.getId(), user.getFirstName(), user.getLastName(), user.getUserName());
            sender.execute(new SendMessage(ctx.chatId().toString(),"Hello " + user.getFirstName() + "! Welcome to Collaboration Bot. Here you can manage projects and track tasks.\n"));
        } catch (WrongArgumentAmountException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), this.toString()));
        }
    }
}
