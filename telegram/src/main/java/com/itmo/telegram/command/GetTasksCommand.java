package com.itmo.telegram.command;

import com.itmo.telegram.constants.Constants;
import com.itmo.telegram.entity.Task;
import com.itmo.telegram.entity.User;
import com.itmo.telegram.exception.UserNotFoundException;
import com.itmo.telegram.exception.WrongArgumentAmountException;
import com.itmo.telegram.service.TaskService;
import com.itmo.telegram.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
public class GetTasksCommand extends AbstractCommand{

    private final UserService userService;
    private final TaskService taskService;

    public GetTasksCommand(UserService userService, TaskService taskService) {
        super("/tasks", "Usage: (/tasks)");
        this.userService = userService;
        this.taskService = taskService;
    }


    @Override
    public void handleCommand(MessageContext ctx, SilentSender sender) {
        try{
            String[] args = ctx.update().getMessage().getText().split(" ");
            if (args.length != 1) throw new WrongArgumentAmountException();
            User user = userService.getUserById(ctx.user().getId());
            List<Task> tasks = taskService.getTasks(user);
            if (tasks.isEmpty()) {
                sender.execute(new SendMessage(ctx.chatId().toString(), "You have no tasks"));
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("%-25s %s\n", "Tasks assigned to me", user.getUsername()));
            stringBuilder.append("---------------------------------------------------------------------------------------------\n");
            for (Task task : tasks) {
                stringBuilder.append(task.toStringWithoutAssigned()).append("\n\n");
            }
            stringBuilder.append("---------------------------------------------------------------------------------------------\n");

            sender.execute(new SendMessage(ctx.chatId().toString(), stringBuilder.toString()));
        } catch (WrongArgumentAmountException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), this.toString()));
        } catch (UserNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.USER_NOT_FOUND));
        }
    }
}
