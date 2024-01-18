package com.itmo.telegram.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "_user")
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String username;


    @OneToMany
    private Set<Project> projects;

    public boolean equals(User other){
        if (!this.id.equals(other.getId())) return false;
        if (!this.firstName.equals(other.getFirstName())) return false;
        if (!this.lastName.equals(other.getLastName())) return false;
        if (!this.username.equals(other.getUsername())) return false;
        return true;
    }
}
