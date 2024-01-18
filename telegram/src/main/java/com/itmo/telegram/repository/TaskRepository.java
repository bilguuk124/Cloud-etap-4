package com.itmo.telegram.repository;

import com.itmo.telegram.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo_Id(Long id);

    Optional<Task> findTaskById(Long id);

    List<Task> findByAssignedTo_IdOrderByEndDateDesc(Long id);

    List<Task> findByAssignedTo_IdOrderByEndDateAsc(Long id);

    @Query("select t from Task t where t.assignedTo.id = ?1 order by t.endDate, t.priority DESC")
    List<Task> findByAssignedTo_IdOrderByEndDateAscPriorityDesc(Long id);
}