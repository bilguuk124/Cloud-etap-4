package com.itmo.telegram.bot;


import lombok.extern.slf4j.Slf4j;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


@Slf4j
public class ResponseHandler {

    private final SilentSender sender;
    private final CommandManager commandManager;


    public ResponseHandler(SilentSender sender, CommandManager commandManager){
        this.sender = sender;
        this.commandManager = commandManager;
    }


    public void startCommand(MessageContext context) {
        log.info("Got request to register");
        commandManager.startCommand(context, sender);
    }
    public void helpCommand(MessageContext context) {
        log.info("Got request to help command");
        commandManager.helpCommand(context, sender);
    }
    public void getProjects(MessageContext context) {
        log.info("Got request to get projects command");
        commandManager.getProjects(context, sender);
    }
    public void getTasks(MessageContext context) {
        log.info("Got request to get tasks command");
        commandManager.getTasks(context, sender);
    }
    public void createProject(MessageContext context) {
        log.info("Got request to create project command");
        commandManager.createProject(context, sender);
    }
    public void createTask(MessageContext context) {
        log.info("Got request to create task command");
        commandManager.createTask(context, sender);
    }

    public void updateTask(MessageContext context) {
        log.info("Got request to update task command");
        commandManager.updateTask(context, sender);
    }
    public void updateProject(MessageContext context) {
        log.info("Got request to update project command");
        commandManager.updateProject(context, sender);
    }
    public void beginTask(MessageContext context) {
        log.info("Got request to begin task command");
        commandManager.beginTask(context, sender);
    }
    public void finishTask(MessageContext context) {
        log.info("Got request to finish task command");
        commandManager.finishTask(context, sender);
    }
    public void joinProject(MessageContext context) {
        log.info("Got request to join a project command");
        commandManager.joinProject(context, sender);
    }
    public void leaveProjectCommand(MessageContext context) {
        log.info("Got request to leave the project command");
        commandManager.leaveProjectCommand(context, sender);
    }

    public void deleteProject(MessageContext context) {
        log.info("Got request to join a project command");
        commandManager.deleteProject(context, sender);
    }
    public void deleteTask(MessageContext context) {
        log.info("Got request to leave the project command");
        commandManager.deleteTask(context, sender);
    }

    public void assignToMe(MessageContext context){
        log.info("Got request to assign a task");
        commandManager.assignToMeCommand(context, sender);
    }

    public void disassignTask(MessageContext context){
        log.info("Got request to disassign a task");
        commandManager.disassignToMeCommand(context, sender);
    }

    public void wrongInput(MessageContext context) {
        log.info("Got request to disassign a task");
        sender.execute(new SendMessage(context.chatId().toString(), "Wrong input! Use /help to check"));
    }

    public void test(MessageContext context) {
        commandManager.test(context);
    }
}
