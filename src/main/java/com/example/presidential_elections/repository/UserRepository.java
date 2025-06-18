package com.example.presidential_elections.repository;

import com.example.presidential_elections.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ===================== LOGIN CHECK =====================
    User findByUsername(String username);
    User findByEmail(String email);

    // ===================== SIGNUP CHECK =====================
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}