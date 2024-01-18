package com.itmo.telegram.command;

import com.itmo.telegram.constants.Constants;
import com.itmo.telegram.entity.Project;
import com.itmo.telegram.entity.Task;
import com.itmo.telegram.entity.User;
import com.itmo.telegram.exception.UserNotFoundException;
import com.itmo.telegram.service.ProjectService;
import com.itmo.telegram.service.UserService;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class GetProjectsCommand extends AbstractCommand {

    private final ProjectService projectService;
    private final UserService userService;

    public GetProjectsCommand(ProjectService projectService, UserService userService) {
        super("/projects", "Usage: (/projects)");
        this.projectService = projectService;
        this.userService = userService;
    }

    @Override
    public void handleCommand(MessageContext ctx, SilentSender sender) {
        try{
            User user = userService.getUserById(ctx.user().getId());

            List<Project> projects = projectService.getProjects(user.getId());

            if (projects.isEmpty()) {
                sender.execute(new SendMessage(ctx.chatId().toString(), "You have no projects"));
                return;
            }


            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("%-30s %s\n\n","","Projects"));
            for (Project project : projects){
                stringBuilder.append(project.toString()).append("\n\n");
            }

            sender.execute(new SendMessage(ctx.chatId().toString(), stringBuilder.toString()));
        } catch (UserNotFoundException e) {
            sender.execute(new SendMessage(ctx.chatId().toString(), Constants.USER_NOT_FOUND));
        }
    }
}
