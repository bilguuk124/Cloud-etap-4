package com.itmo.telegram.command;

import com.itmo.telegram.constants.Constants;
import com.itmo.telegram.entity.User;
import com.itmo.telegram.exception.NotEnoughRightsException;
import com.itmo.telegram.exception.TaskNotFoundException;
import com.itmo.telegram.exception.UserNotFoundException;
import com.itmo.telegram.exception.WrongArgumentAmountException;
import com.itmo.telegram.service.TaskService;
import com.itmo.telegram.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class DeleteTaskCommand extends AbstractCommand{

    private final UserService userService;
    private final TaskService taskService;

    public DeleteTaskCommand(UserService userService, TaskService taskService) {
        super("/delete_task", "Usage: (/delete_task (task_id)");
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
            taskService.deleteTask(user, id);
            sender.execute(new SendMessage(ctx.chatId().toString(),"Task " + args[1] + " was deleted successfully!"));
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
        }
    }
}
