package com.itmo.telegram.repository;

import com.itmo.telegram.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    @NotNull
    @Override
    Optional<User> findById(Long id);
}