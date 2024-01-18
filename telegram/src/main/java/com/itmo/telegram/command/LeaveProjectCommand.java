package com.itmo.telegram.command;

import com.itmo.telegram.constants.Constants;
import com.itmo.telegram.entity.User;
import com.itmo.telegram.exception.*;
import com.itmo.telegram.service.ProjectService;
import com.itmo.telegram.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class LeaveProjectCommand extends AbstractCommand{

    private final UserService userService;
    private final ProjectService projectService;

    public LeaveProjectCommand(UserService userService, ProjectService projectService) {
        super("/leave", "Usage: (/leave (project-id))");
        this.userService = userService;
        this.projectService = projectService;
    }


    @Override
    public void handleCommand(MessageContext ctx, SilentSender sender) {
        try{
            String[] args = ctx.update().getMessage().getText().split(" ");
            if (args.length != 2) throw new WrongArgumentAmountException();
            User user = userService.getUserById(ctx.user().getId());
            Long projectId = Long.parseLong(args[1]);
            projectService.leaveProject(projectId, user);
            sender.execute(new SendMessage(ctx.chatId().toString(),"Project " + args[1] + " was updated successfully!"));
        } catch (WrongArgumentAmountException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), this.toString()));
        } catch (UserNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.USER_NOT_FOUND));
        } catch (ProjectNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.PROJECT_NOT_FOUND));
        } catch (NotPartOfThisProjectException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), "You are not part of this project"));
        } catch (NumberFormatException e){
            sender.execute(new SendMessage(ctx.chatId().toString(), "Id must be a number!"));
        }
    }
}
