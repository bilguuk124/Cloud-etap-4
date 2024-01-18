package com.itmo.telegram.bot;

import com.itmo.library.NotificationEntity;
import com.itmo.library.UserInfo;
import com.itmo.telegram.command.*;
import com.itmo.telegram.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandManager {
    private final KafkaService kafkaService;
    private List<Command> commands = new ArrayList<>();

    private final StartCommand startCommand;
    private final GetTasksCommand getTasks;
    private final GetProjectsCommand getProjects;
    private final CreateProjectCommand createProject;
    private final CreateTaskCommand createTask;
    private final UpdateTaskCommand updateTask;
    private final UpdateProjectCommand updateProject;
    private final DeleteProjectCommand deleteProject;
    private final DeleteTaskCommand deleteTask;
    private final BeginTaskCommand beginTask;
    private final FinishTaskCommand finishTask;
    private final JoinProjectCommand joinProject;
    private final LeaveProjectCommand leaveProjectCommand;
    private final AssignToMeCommand assignToMeCommand;
    private final DisassignTaskCommand disassignTaskCommand;

    @PostConstruct
    public void init(){
        commands.add(startCommand);
        commands.add(getTasks);
        commands.add(getProjects);
        commands.add(createProject);
        commands.add(createTask);
        commands.add(updateTask);
        commands.add(updateProject);
        commands.add(deleteProject);
        commands.add(deleteTask);
        commands.add(beginTask);
        commands.add(finishTask);
        commands.add(joinProject);
        commands.add(leaveProjectCommand);
        commands.add(assignToMeCommand);
        commands.add(disassignTaskCommand);
    }


    public String helpString(){
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%-40s %-60s\n", "Command", "Usage"));
        builder.append("--------------------------------------------------------------------------------\n");
        for (Command command : commands){
            builder.append(String.format("%-40s %-60s\n",
                    command.getName(),
                    command.getDescription()));
        }
        return builder.toString();
    }

    public void startCommand(MessageContext context, SilentSender sender) {
        startCommand.handleCommand(context, sender);
    }
    public void helpCommand(MessageContext context, SilentSender sender) {
        sender.execute(new SendMessage(context.chatId().toString(), helpString()));
    }
    public void getProjects(MessageContext context, SilentSender sender) {
        getProjects.handleCommand(context, sender);
    }
    public void getTasks(MessageContext context, SilentSender sender) {
        getTasks.handleCommand(context, sender);
    }
    public void createProject(MessageContext context, SilentSender sender) {
        createProject.handleCommand(context, sender);
    }
    public void createTask(MessageContext context, SilentSender sender) {
        createTask.handleCommand(context, sender);
    }

    public void updateTask(MessageContext context, SilentSender sender) {
        updateTask.handleCommand(context, sender);
    }
    public void updateProject(MessageContext context, SilentSender sender) {
        updateProject.handleCommand(context, sender);
    }
    public void beginTask(MessageContext context, SilentSender sender) {
        beginTask.handleCommand(context, sender);
    }
    public void finishTask(MessageContext context, SilentSender sender) {
        finishTask.handleCommand(context, sender);
    }
    public void joinProject(MessageContext context, SilentSender sender) {
        joinProject.handleCommand(context, sender);
    }
    public void leaveProjectCommand(MessageContext context, SilentSender sender) {
        leaveProjectCommand.handleCommand(context, sender);
    }

    public void deleteProject(MessageContext context, SilentSender sender) {
        deleteProject.handleCommand(context, sender);
    }
    public void deleteTask(MessageContext context, SilentSender sender) {
        deleteTask.handleCommand(context, sender);
    }


    public void assignToMeCommand(MessageContext context, SilentSender sender) {
        assignToMeCommand.handleCommand(context, sender);
    }

    public void disassignToMeCommand(MessageContext context, SilentSender sender) {
        disassignTaskCommand.handleCommand(context, sender);
    }

    public void test(MessageContext context) {
        kafkaService.sendMessage(NotificationEntity.builder()
                .receivers(List.of(new UserInfo(context.chatId(), context.user().getUserName())))
                .message("Testing Kafka")
                .build());

    }
}
