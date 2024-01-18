package com.itmo.telegram.service;

import com.itmo.library.NotificationEntity;
import com.itmo.library.UserInfo;
import com.itmo.telegram.entity.*;
import com.itmo.telegram.exception.*;
import com.itmo.telegram.repository.ProjectRepository;
import com.itmo.telegram.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final KafkaService producer;
    private final String exchange = "internal.exchange";
    private final String routingKey = "internal.notification.routing-key";

    public List<Task> getTasks(User user) {
        return repository.findByAssignedTo_IdOrderByEndDateAscPriorityDesc(user.getId());
    }

    public void deleteTask(User user, Long id) throws TaskNotFoundException, NotEnoughRightsException {
        Task task = repository.findById(id).orElseThrow(TaskNotFoundException::new);
        Project project = projectService.getProjectByTask(task);
        if (!project.getCreator().equals(user)) throw new NotEnoughRightsException();
        NotificationEntity notification = NotificationEntity.builder()
                .receivers(project.getParticipants().stream().filter(e -> !e.equals(user)).map(p -> new UserInfo(p.getId(), p.getUsername())).toList())
                .message("Task " + task.getTitle() + " is deleted!")
                .build();
        repository.deleteById(id);
        producer.sendMessage(notification);
    }

    public void createTask(Long projectId, String taskName, Priority priority, LocalDate endDate, User user) throws WrongEnumArgument, NotEnoughRightsException, ProjectNotFoundException {
        Project project = projectService.getProjectById(projectId);
        if (!project.getCreator().equals(user)) throw new NotEnoughRightsException();
        Task task = Task.builder()
                .title(taskName)
                .priority(priority)
                .assignedTo(null)
                .project(project)
                .creationDate(LocalDate.now())
                .endDate(endDate)
                .status(TaskStatus.NOT_STARTED)
                .build();

        NotificationEntity notification = NotificationEntity.builder()
                .receivers(project.getParticipants().stream().filter(e -> !e.equals(user)).map(p -> new UserInfo(p.getId(), p.getUsername())).toList())
                .message("New task incoming! " + task.getTitle() + " was created! Quickly assign it to yourself")
                .build();

        project.getTasks().add(task);
        repository.save(task);
        projectRepository.save(project);
        producer.sendMessage(notification);

    }

    public void updateTask(Long id, String taskName, Priority priority, LocalDate endDate, User user) throws NotEnoughRightsException, TaskNotFoundException, ProjectNotFoundException {
        Task task = repository.findTaskById(id).orElseThrow(TaskNotFoundException::new);
        Project project = task.getProject();
        if (!project.getCreator().equals(user) && !task.getAssignedTo().equals(user)) throw new NotEnoughRightsException();
        task.setTitle(taskName);
        task.setPriority(priority);
        task.setEndDate(endDate);

        NotificationEntity notification = NotificationEntity.builder()
                .receivers(project.getParticipants().stream().filter(e -> !e.equals(user)).map(p -> new UserInfo(p.getId(), p.getUsername())).toList())
                .message("Task " + task.getTitle() + " was updated! " + task.toStringWithAssigned())
                .build();

        repository.save(task);
        producer.sendMessage(notification);
    }

    public void beginTask(Long id, User user) throws NotEnoughRightsException, TaskNotFoundException, ProjectNotFoundException, NotAssignedException {
        Task task = repository.findTaskById(id).orElseThrow(TaskNotFoundException::new);
        if (!task.getAssignedTo().equals(user))
            throw new NotAssignedException("User " + user.getUsername() + " is not assigned to this task");
        task.setStatus(TaskStatus.IN_PROGRESS);

        NotificationEntity notification = NotificationEntity.builder()
                .receivers(task.getProject().getParticipants().stream().filter(e -> !e.equals(user)).map(p -> new UserInfo(p.getId(), p.getUsername())).toList())
                .message("Task " + task.getTitle() + " has begun! " + task.toStringWithAssigned())
                .build();
        repository.save(task);
        producer.sendMessage(notification);

    }

    public void endTask(Long id, User user) throws NotEnoughRightsException, TaskNotFoundException, ProjectNotFoundException, NotAssignedException {
        Task task = repository.findTaskById(id).orElseThrow(TaskNotFoundException::new);
        if (!task.getAssignedTo().equals(user)) throw new NotAssignedException("User " + user.getUsername() + " is not assigned to this task") ;
        task.setStatus(TaskStatus.FINISHED);

        NotificationEntity notification = NotificationEntity.builder()
                .receivers(task.getProject().getParticipants().stream().filter(e -> !e.equals(user)).map(p -> new UserInfo(p.getId(), p.getUsername())).toList())
                .message("Task " + task.getTitle() + " has ended! +1 for the team." + task.toStringWithAssigned())
                .build();
        repository.save(task);
        producer.sendMessage(notification);
    }

    public void assignToMe(Long id, User user) throws TaskNotFoundException, AlreadyAssignedException {
        Task task = repository.findTaskById(id).orElseThrow(TaskNotFoundException::new);
        if (task.getAssignedTo() != null) throw new AlreadyAssignedException();
        task.setAssignedTo(user);

        NotificationEntity notification = NotificationEntity.builder()
                .receivers(task.getProject().getParticipants().stream().filter(e -> !e.equals(user)).map(p -> new UserInfo(p.getId(), p.getUsername())).toList())
                .message("Task " + task.getTitle() + " was assigned to " + user.getUsername())
                .build();
        repository.save(task);
        producer.sendMessage(notification);
    }
    public void disAssign(Long id, User user) throws TaskNotFoundException, AlreadyAssignedException {
        Task task = repository.findTaskById(id).orElseThrow(TaskNotFoundException::new);
        if (task.getAssignedTo().equals(user)) throw new AlreadyAssignedException();
        task.setAssignedTo(null);
        NotificationEntity notification = NotificationEntity.builder()
                .receivers(task.getProject().getParticipants().stream().filter(e -> !e.equals(user)).map(p -> new UserInfo(p.getId(), p.getUsername())).toList())
                .message("Task " + task.getTitle() + " is now unassigned! Quickly get assigned")
                .build();
        repository.save(task);
        producer.sendMessage(notification);
    }

    public List<Task> getAllTasks(){
        return repository.findAll();
    }
}
