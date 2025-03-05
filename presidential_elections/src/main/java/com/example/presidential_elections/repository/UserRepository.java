package com.example.presidential_elections.repository;

import com.example.presidential_elections.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // for login check
    User findByUsername(String username);
    User findByEmail(String email);

    // for signup check
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
