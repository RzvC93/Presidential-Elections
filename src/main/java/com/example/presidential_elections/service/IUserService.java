package com.example.presidential_elections.service;

import com.example.presidential_elections.dto.UserDTO;
import com.example.presidential_elections.model.User;

import java.util.Optional;

public interface IUserService {

    // ===================== AUTH =====================
    User registerUser(UserDTO userDTO);
    User loginUser(String username, String password);

    // ===================== PROFILE =====================
    User findByUsername(String username);
    void updateUser(String loggedInUsername, UserDTO userDTO);

    // ===================== DELETE =====================
    void deleteUser(String inputUsername, String loggedInUsername);
}