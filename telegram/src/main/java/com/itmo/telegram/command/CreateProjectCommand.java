package com.itmo.telegram.command;

import com.itmo.telegram.constants.Constants;
import com.itmo.telegram.entity.User;
import com.itmo.telegram.exception.UserNotFoundException;
import com.itmo.telegram.exception.WrongArgumentAmountException;
import com.itmo.telegram.service.ProjectService;
import com.itmo.telegram.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class CreateProjectCommand extends AbstractCommand{

    private final UserService userService;
    private final ProjectService projectService;

    public CreateProjectCommand(UserService userService, ProjectService projectService) {
        super("/create_project", "Usage: (/create_project (project-name))");
        this.userService = userService;
        this.projectService = projectService;
    }


    @Override
    public void handleCommand(MessageContext ctx, SilentSender sender) {
        try{
            String[] args = ctx.update().getMessage().getText().split(" ");
            if (args.length != 2) throw new WrongArgumentAmountException();
            User user = userService.getUserById(ctx.user().getId());
            projectService.createProject(user, args[1]);
            sender.execute(new SendMessage(ctx.chatId().toString(),"Project " + args[1] + " was created successfully!"));
        } catch (WrongArgumentAmountException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), this.toString()));
        } catch (UserNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.USER_NOT_FOUND));
        }
    }
}
