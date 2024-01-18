package com.itmo.telegram.repository;

import com.itmo.telegram.entity.Project;
import com.itmo.telegram.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByParticipants_Id(Long id);

    Project findByTasks_Id(Long id);

    Optional<Project> getProjectById(Long projectId);

    List<Project> findByParticipants_IdOrderByIdAsc(Long id);
}