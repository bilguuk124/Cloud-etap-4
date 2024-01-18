package com.itmo.telegram.command;

import com.itmo.telegram.constants.Constants;
import com.itmo.telegram.entity.Priority;
import com.itmo.telegram.entity.User;
import com.itmo.telegram.exception.*;
import com.itmo.telegram.service.TaskService;
import com.itmo.telegram.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class BeginTaskCommand extends AbstractCommand{

    private final UserService userService;
    private final TaskService taskService;

    public BeginTaskCommand(UserService userService, TaskService taskService) {
        super("/begin", "Usage: (/begin (task_id)");
        this.userService = userService;
        this.taskService = taskService;
    }


    @Override
    public void handleCommand(MessageContext ctx, SilentSender sender) {
        try{
            String[] args = ctx.update().getMessage().getText().split(" ");
            if (args.length != 2) throw new WrongArgumentAmountException();
            Long id = Long.parseLong(args[1]);
            User user = userService.getUserById(ctx.user().getId());
            taskService.beginTask(id, user);
            sender.execute(new SendMessage(ctx.chatId().toString(),"Task " + args[1] + " has begun!"));
        } catch (WrongArgumentAmountException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), this.toString()));
        } catch (UserNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.USER_NOT_FOUND));
        } catch (NumberFormatException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.NUMBER_FORMAT));
        } catch (TaskNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.TASK_NOT_FOUND));
        } catch (NotEnoughRightsException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.NOT_ENOUGH_RIGHTS));
        } catch (ProjectNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), "Project does not exist"));
        } catch (NotAssignedException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), e.getMessage()));
        }
    }
}
