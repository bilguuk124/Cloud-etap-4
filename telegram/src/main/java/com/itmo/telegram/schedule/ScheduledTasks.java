package com.itmo.telegram.schedule;

import com.itmo.library.NotificationEntity;
import com.itmo.library.UserInfo;
import com.itmo.telegram.entity.Task;
import com.itmo.telegram.entity.User;
import com.itmo.telegram.service.KafkaService;
import com.itmo.telegram.service.TaskService;
import com.itmo.telegram.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ScheduledTasks {
    private final TaskService taskService;
    private final KafkaService kafkaService;
    private final UserService userService;


    @Scheduled(cron = "0 0 14 * * *")    // Every day at 14:00
    public void NotifyAssignees(){
        log.info("Starting to notify assignees");
        List<Task> tasks = taskService.getAllTasks();

        for (Task task : tasks){
            long daysBeforeEnding = ChronoUnit.DAYS.between(LocalDate.now(), task.getEndDate());
            if (daysBeforeEnding <= 3) {
                NotificationEntity notification;
                if (task.getAssignedTo() == null){
                    notification = NotificationEntity.builder()
                            .receivers(task.getProject().getParticipants().stream().map(e -> new UserInfo(e.getId(), e.getUsername())).toList())
                            .message("Unassigned task " + task.getTitle() + " of project " + task.getProject().getName() + " is about to end! Get assigned to it as soon as possible!")
                            .build();
                }
                else{
                    notification = NotificationEntity.builder()
                            .receivers(List.of(new UserInfo(task.getAssignedTo().getId(), task.getAssignedTo().getUsername())))
                            .message("Your task " + task.getTitle() + " is about to expire! Work on it as soon as possible")
                            .build();
                }
                kafkaService.sendMessage(notification);
            }
        }
        log.info("Ended to notify assignees");
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void GoodMorning(){
        log.info("Starting to notify good morning");
        List<User> users = userService.getUsers();

        for (User user: users){
            List<Task> tasks = taskService.getTasks(user);
            LocalDate now = LocalDate.now();
            StringBuilder stringBuilder  = new StringBuilder();
            stringBuilder.append("Good morning! ")
                    .append(user.getFirstName())
                    .append(" ")
                    .append(user.getLastName())
                    .append("!\n")
                    .append("Today is ")
                    .append(now.getDayOfWeek().toString())
                    .append(" ")
                    .append(now.getMonth().toString())
                    .append(" ")
                    .append(now.getDayOfMonth());
            stringBuilder.append(String.format("\n%-25s %s\n", "Tasks assigned to me", user.getUsername()));
            stringBuilder.append("---------------------------------------------------------------------------------------------\n");
            for (Task task : tasks) {
                stringBuilder.append(task.toStringWithoutAssigned()).append("\n\n");
            }
            stringBuilder.append("---------------------------------------------------------------------------------------------\n");
            NotificationEntity notification = NotificationEntity
                    .builder()
                    .receivers(List.of(new UserInfo(user.getId(), user.getUsername())))
                    .message(stringBuilder.toString())
                    .build();
            kafkaService.sendMessage(notification);
        }

        log.info("Ended to notify good morning");
    }



}
