package com.itmo.telegram.service;

import com.itmo.library.NotificationEntity;
import com.itmo.library.UserInfo;
import com.itmo.telegram.entity.Project;
import com.itmo.telegram.entity.Task;
import com.itmo.telegram.entity.User;
import com.itmo.telegram.exception.AlreadyJoinedException;
import com.itmo.telegram.exception.NotEnoughRightsException;
import com.itmo.telegram.exception.NotPartOfThisProjectException;
import com.itmo.telegram.exception.ProjectNotFoundException;
import com.itmo.telegram.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final KafkaService producer;

    private final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    public List<Project> getProjects(Long userId) {
        return repository.findByParticipants_IdOrderByIdAsc(userId);
    }

    public void createProject(User user, String projectName) {
        Project project = Project.builder()
                .name(projectName)
                .tasks(new ArrayList<>())
                .creator(user)
                .participants(Set.of(user))
                .build();

        repository.saveAndFlush(project);
    }

    public void deleteProject(User user, Long id) throws ProjectNotFoundException, NotEnoughRightsException {
        Project project = repository.findById(id).orElseThrow(ProjectNotFoundException::new);
        if (!project.getCreator().equals(user)) throw new NotEnoughRightsException();
        NotificationEntity notification = NotificationEntity.builder()
                .receivers(project.getParticipants().stream().map(p -> new UserInfo(p.getId(), p.getUsername())).toList())
                .message("Project " + project.getName() + " is deleted")
                .build();
        repository.deleteById(id);
        producer.sendMessage(notification);
    }

    public void updateProject(User user, Long id, String newName) throws ProjectNotFoundException, NotEnoughRightsException {
        Project project = repository.findById(id).orElseThrow(ProjectNotFoundException::new);
        if (!project.getCreator().equals(user)) throw new NotEnoughRightsException();
        NotificationEntity notification = NotificationEntity.builder()
                .receivers(project.getParticipants().stream().map(p -> new UserInfo(p.getId(), p.getUsername())).toList())
                .message("Project " + project.getName() + " is updated! New name = " + newName)
                .build();
        project.setName(newName);
        producer.sendMessage(notification);
        repository.saveAndFlush(project);
    }

    public List<Task> getTasksById(Long projectId) throws ProjectNotFoundException {
        return getProjectById(projectId).getTasks();
    }

    public Project getProjectByTask(Task task) {
        return repository.findByTasks_Id(task.getId());
    }

    public Project getProjectById(Long projectId) throws ProjectNotFoundException {
        return repository.getProjectById(projectId).orElseThrow(ProjectNotFoundException::new);
    }

    public void joinProject(Long projectId, User user) throws ProjectNotFoundException, AlreadyJoinedException {
        Project project = repository.getProjectById(projectId).orElseThrow(ProjectNotFoundException::new);
        if (isParticipant(project.getParticipants(), user)) {
            logger.info("Logging list of participants {}", Arrays.toString(project.getParticipants().stream().map(User::getUsername).toArray()) );
            throw new AlreadyJoinedException("You have already joined this project");
        }
        NotificationEntity notification = NotificationEntity.builder()
                .receivers(project.getParticipants().stream().map(p -> new UserInfo(p.getId(), p.getUsername())).toList())
                .message(user.getUsername() + " just joined project " + project.getName() + "!")
                .build();
        project.getParticipants().add(user);
        repository.saveAndFlush(project);
        producer.sendMessage(notification);
    }

    public void leaveProject(Long projectId, User user) throws ProjectNotFoundException, NotPartOfThisProjectException {
        Project project = repository.getProjectById(projectId).orElseThrow(ProjectNotFoundException::new);
        if (!isParticipant(project.getParticipants(), user)) {
            logger.info("Logging list of participants {}", Arrays.toString(project.getParticipants().stream().map(User::getUsername).toArray()) );
            throw new NotPartOfThisProjectException("You are not part of this exception");
        }
        if (!project.getCreator().equals(user)){
            logger.info("User leaving project");
            NotificationEntity notification = NotificationEntity.builder()
                    .receivers(project.getParticipants().stream().map(p -> new UserInfo(p.getId(), p.getUsername())).toList())
                    .message(user.getUsername() + " just left project " + project.getName() + "!")
                    .build();
            producer.sendMessage(notification);
            project.getParticipants().remove(user);
            repository.saveAndFlush(project);
        }else{
            NotificationEntity notification = NotificationEntity.builder()
                    .receivers(project.getParticipants().stream().map(p -> new UserInfo(p.getId(), p.getUsername())).toList())
                    .message(user.getUsername() + " just left project " + project.getName() + "! The project will be deleted!")
                    .build();
            logger.info("User is creator, project is being deleted");
            producer.sendMessage(notification);
            repository.deleteById(projectId);
        }

    }

    private boolean isParticipant(Set<User> users, User user){
        boolean isParticipant = false;
        for (User user1 : users){
            if (user1.getId().equals(user.getId())) {
                isParticipant = true;
                break;
            }
        }
        return isParticipant;
    }
}
