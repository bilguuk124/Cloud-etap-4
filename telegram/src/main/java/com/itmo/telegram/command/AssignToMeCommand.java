package com.itmo.telegram.command;

import com.itmo.telegram.constants.Constants;
import com.itmo.telegram.entity.User;
import com.itmo.telegram.exception.*;
import com.itmo.telegram.service.TaskService;
import com.itmo.telegram.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class AssignToMeCommand extends AbstractCommand{

    private final UserService userService;
    private final TaskService taskService;

    public AssignToMeCommand(UserService userService, TaskService taskService) {
        super("/assign", "Usage: /assign (task_id)");
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
            taskService.assignToMe(id, user);
            sender.execute(new SendMessage(ctx.chatId().toString(),"Successfully assigned to you!"));
        } catch (WrongArgumentAmountException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), this.toString()));
        } catch (UserNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.USER_NOT_FOUND));
        } catch (NumberFormatException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.NUMBER_FORMAT));
        } catch (TaskNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.TASK_NOT_FOUND));
        } catch (AlreadyAssignedException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), "This task is already assigned"));
        }
    }
}
