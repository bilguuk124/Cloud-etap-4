package com.itmo.telegram.command;

import com.itmo.telegram.constants.Constants;
import com.itmo.telegram.entity.Priority;
import com.itmo.telegram.entity.Task;
import com.itmo.telegram.entity.User;
import com.itmo.telegram.exception.*;
import com.itmo.telegram.service.TaskService;
import com.itmo.telegram.service.UserService;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class CreateTaskCommand extends AbstractCommand{

    private final UserService userService;
    private final TaskService taskService;

    public CreateTaskCommand(UserService userService, TaskService taskService) {
        super("/create_task", "Usage: (/create_task (project-id) (task-name) (priority(0,1,2)) (end date (2023-10-23)))");
        this.userService = userService;
        this.taskService = taskService;
    }


    @Override
    public void handleCommand(MessageContext ctx, SilentSender sender) {
        try{
            String[] args = ctx.update().getMessage().getText().split(" ");
            if (args.length != 5) throw new WrongArgumentAmountException();
            Long projectId = Long.parseLong(args[1]);
            User user = userService.getUserById(ctx.user().getId());
            Priority priority = Priority.getByNumber(Integer.parseInt(args[3].trim()));
            LocalDate endDate = LocalDate.parse(args[4], new DateTimeFormatterFactory("yyyy-MM-dd").createDateTimeFormatter());
            taskService.createTask(projectId, args[2], priority, endDate, user);
            sender.execute(new SendMessage(ctx.chatId().toString(),"Task " + args[2] + " was created successfully!"));
        } catch (WrongArgumentAmountException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), this.toString()));
        } catch (UserNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.USER_NOT_FOUND));
        } catch (WrongEnumArgument e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), e.getMessage()));
        } catch (NotEnoughRightsException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.NOT_ENOUGH_RIGHTS));
        } catch (ProjectNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), "Project does not exist"));
        } catch (DateTimeParseException e){
            sender.execute(new SendMessage(ctx.chatId().toString(), "Date format is wrong!"));
        } catch (NumberFormatException e){
            sender.execute(new SendMessage(ctx.chatId().toString(), "Number variables are wrong! Number variables: id, priority"));
        }
    }
}
