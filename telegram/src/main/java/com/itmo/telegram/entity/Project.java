package com.itmo.telegram.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_gen")
    @SequenceGenerator(name = "project_gen", sequenceName = "project_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private List<Task> tasks;

    @ManyToOne
    private User creator;

    @ManyToMany
    private Set<User> participants;


    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%-10s %-50s\n",id, name));
        stringBuilder.append("Creator: ").append(creator.getUsername()).append("\n");
        stringBuilder.append("----------------------------------------------------------------------------------\n");
        stringBuilder.append(String.format("%25s\n\n", "||Tasks||"));
        stringBuilder.append(String.format("%-10s %-25s %-35s %-30s\n", "id","title","status","assigned"));
        for (Task task : tasks){
            String assigned = task.getAssignedTo() == null ? "Not assigned" : task.getAssignedTo().getUsername();
            stringBuilder.append(String.format("%-10s %-15s %-30s %-30s\n", task.getId(),task.getTitle(),task.getStatus(),assigned));
        }
        stringBuilder.append("----------------------------------------------------------------------------------\n\n");
        stringBuilder.append(String.format("%25s\n\n", "||Participants||"));
        stringBuilder.append(String.format("%-10s %-25s %-25s %-25s\n", "№","first name","last name","username"));
        int i = 1;
        for (User user : participants){
            stringBuilder.append(String.format("%-10s %-25s %-25s %-25s\n", i, user.getFirstName(),user.getLastName(),user.getUsername()));
            i++;
        }
        stringBuilder.append("\n\n");
        stringBuilder.append("----------------------------------------------------------------------------------\n\n");
        return stringBuilder.toString();
    }
}
