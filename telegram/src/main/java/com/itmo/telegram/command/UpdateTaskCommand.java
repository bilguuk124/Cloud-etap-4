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
public class UpdateTaskCommand extends AbstractCommand{

    private final UserService userService;
    private final TaskService taskService;

    public UpdateTaskCommand(UserService userService, TaskService taskService) {
        super("/update_task", "Usage: (/update_task (task_id) (new_name) (priority(0,1,2)) (end date (2023-10-23)))");
        this.userService = userService;
        this.taskService = taskService;
    }


    @Override
    public void handleCommand(MessageContext ctx, SilentSender sender) {
        try{
            String[] args = ctx.update().getMessage().getText().split(" ");
            if (args.length != 5) throw new WrongArgumentAmountException();
            Long id = Long.parseLong(args[1]);
            User user = userService.getUserById(ctx.user().getId());
            LocalDate endDate = LocalDate.parse(args[4], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            taskService.updateTask(id, args[2], Priority.getByNumber(Integer.valueOf(args[3])), endDate, user);
            sender.execute(new SendMessage(ctx.chatId().toString(),"Task " + args[1] + " was updated successfully!"));
        } catch (WrongArgumentAmountException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), this.toString()));
        } catch (UserNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.USER_NOT_FOUND));
        } catch (NumberFormatException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.NUMBER_FORMAT + " - priority"));
        } catch (TaskNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.TASK_NOT_FOUND));
        } catch (NotEnoughRightsException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.NOT_ENOUGH_RIGHTS));
        } catch (WrongEnumArgument e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), e.getMessage()));
        } catch (ProjectNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), "Project does not exist"));
        }
    }
}
