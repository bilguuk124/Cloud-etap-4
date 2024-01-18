package com.itmo.telegram.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_gen")
    @SequenceGenerator(name = "task_gen", sequenceName = "task_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    private String title;
    private LocalDate creationDate;
    private LocalDate endDate;
    private Priority priority;
    private TaskStatus status;

    @ManyToOne
    private Project project;

    @ManyToOne
    private User assignedTo;

    public String toStringWithAssigned(){
        return String.format("%-30s %-30s Project- %s\n", id, title, project.getName()) +
                "___________________________________________________________________\n" +
                String.format("%-20s %-15s %-15s %-15s %-25s\n", "Created", "End", "Priority", "Status", "Assigned") +
                String.format("%-15s %-15s %-10s %-15s %-20s\n", creationDate, endDate, priority.toString().toLowerCase(), status.toString().toLowerCase(), assignedTo.getUsername()) +
                "___________________________________________________________________\n";
    }

    public String toStringWithoutAssigned(){
        return String.format("%-30s %-30s Project- %s\n", id, title, project.getName()) +
                "___________________________________________________________________\n" +
                String.format("%-30s %-30s %-25s %-30s\n", "Created", "End", "Priority", "Status") +
                String.format("%-25s %-25s %-20s %-25s\n", creationDate, endDate, priority.toString().toLowerCase(), status.toString().toLowerCase()) +
                "___________________________________________________________________\n";
    }

    public boolean equals(Task obj) {
        if (!id.equals(obj.getId())) return false;
        if (!title.equals(obj.getTitle())) return false;
        if (!creationDate.equals(obj.getCreationDate())) return false;
        if (!endDate.equals(obj.getEndDate())) return false;
        if (!priority.equals(obj.getPriority())) return false;
        if (!status.equals(obj.getStatus())) return false;
        return true;
    }
}
