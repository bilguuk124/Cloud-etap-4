package com.itmo.telegram.service;

import com.itmo.telegram.entity.User;
import com.itmo.telegram.exception.UserNotFoundException;
import com.itmo.telegram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public void registerUser(Long id, String firstName, String lastName, String username){
        User user = User.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .projects(new HashSet<>())
                .build();

        repository.save(user);
    }

    public User getUserById(Long id) throws UserNotFoundException {
        return repository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public List<User> getUsers() {
        return repository.findAll();
    }
}
