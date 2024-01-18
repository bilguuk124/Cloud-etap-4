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
public class JoinProjectCommand extends AbstractCommand{

    private final UserService userService;
    private final ProjectService projectService;

    public JoinProjectCommand(UserService userService, ProjectService projectService) {
        super("/join", "Usage: (/join (project-id))");
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
            projectService.joinProject(projectId, user);
            sender.execute(new SendMessage(ctx.chatId().toString(),"You have joined project " + args[1]));
        } catch (WrongArgumentAmountException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), this.toString()));
        } catch (UserNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.USER_NOT_FOUND));
        } catch (ProjectNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.PROJECT_NOT_FOUND));
        } catch (AlreadyJoinedException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), "You have already joined this project"));
        } catch (NumberFormatException e){
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.NUMBER_FORMAT));
        }
    }
}
