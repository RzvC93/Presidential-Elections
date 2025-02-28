package com.example.presidential_elections.service;

import com.example.presidential_elections.dto.UserDTO;
import com.example.presidential_elections.model.User;

import java.util.Optional;

public interface IUserService {

    // signup
    User registerUser(UserDTO userDTO);

    // login
    User findByUsername(String username);
    User loginUser(String username, String password);

    // update
    void updateUser(String loggedInUsername, UserDTO userDTO);

    // delete
    void deleteUser(String inputUsername, String loggedInUsername);
}