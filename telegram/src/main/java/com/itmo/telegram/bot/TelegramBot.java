package com.itmo.telegram.bot;

import com.itmo.telegram.constants.Constants;
import com.itmo.telegram.service.UserService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;

@Component
@Getter
@Slf4j
public class TelegramBot extends AbilityBot {

    private final ResponseHandler responseHandler;

    public TelegramBot(Environment env, CommandManager commandManager){
        super(env.getProperty("bot.token"), env.getProperty("bot.name"));
//        this.commandManager = commandManager;
        this.responseHandler = new ResponseHandler(silent(), commandManager);
    }

    public Ability processStart(){
        return Ability
                .builder()
                .name("start")
                .info(Constants.START_DESCRIPTION)
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::startCommand)
                .build();
    }

    public Ability help(){
        return Ability
                .builder()
                .name("help")
                .info("List of commands")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::helpCommand)
                .build();
    }

    public Ability processProjects(){
        return Ability
                .builder()
                .name("projects")
                .info("Your projects:\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::getProjects)
                .build();
    }

    public Ability createProject(){
        return Ability
                .builder()
                .name("create_project")
                .info("Creating a progress\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::createProject)
                .build();
    }

    public Ability deleteProject(){
        return Ability
                .builder()
                .name("delete_project")
                .info("Deleting project\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::deleteProject)
                .build();
    }

    public Ability joinProject(){
        return Ability
                .builder()
                .name("join")
                .info("Joining a progress\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::joinProject)
                .build();
    }

    public Ability leaveProject(){
        return Ability
                .builder()
                .name("leave")
                .info("Leaving a project\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::leaveProjectCommand)
                .build();
    }

    public Ability tasks(){
        return Ability
                .builder()
                .name("tasks")
                .info("Your tasks:\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::getTasks)
                .build();
    }

    public Ability createTask(){
        return Ability
                .builder()
                .name("create_task")
                .info("Creating task:\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::createTask)
                .build();
    }

    public Ability deleteTask(){
        return Ability
                .builder()
                .name("delete_task")
                .info("Deleting task:\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::deleteTask)
                .build();
    }

    public Ability beginTask(){
        return Ability
                .builder()
                .name("begin")
                .info("Beginning task\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::beginTask)
                .build();
    }

    public Ability doneTask(){
        return Ability
                .builder()
                .name("done")
                .info("Finishing task\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::finishTask)
                .build();
    }

    public Ability updateTask(){
        return Ability
                .builder()
                .name("update_task")
                .info("Updating task:\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::updateTask)
                .build();
    }

    public Ability updateProject(){
        return Ability
                .builder()
                .name("update_project")
                .info("Updating project\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::updateProject)
                .build();
    }

    public Ability assignToMe(){
        return Ability
                .builder()
                .name("assign")
                .info("assigning task\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::assignToMe)
                .build();
    }

    public Ability disAssign(){
        return Ability
                .builder()
                .name("disassign")
                .info("disassining task\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::disassignTask)
                .build();
    }
    public Ability test(){
        return Ability
                .builder()
                .name("test")
                .info("test\n")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::test)
                .build();
    }

    public Ability defaultProcess(){
        return Ability
                .builder()
                .name(DEFAULT)
                .info("Wrong input!")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(responseHandler::wrongInput)
                .build();
    }



    @Override
    public long creatorId() {
        return 1L;
    }
}
