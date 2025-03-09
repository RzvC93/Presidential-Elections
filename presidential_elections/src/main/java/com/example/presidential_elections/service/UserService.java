package com.example.presidential_elections.service;

import com.example.presidential_elections.dto.UserDTO;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // signup
    @Override
    public User registerUser(UserDTO userDTO) {

        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match.");
        }

        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username '" + userDTO.getUsername() + "' is already taken.");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email '" + userDTO.getEmail() + "' is already in use.");
        }

        if (userDTO.getAge() < 18) {
            throw new RuntimeException("Registration failed: Age must be 18 or older.");
        }

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setMiddleName(userDTO.getMiddleName());
        user.setLastName(userDTO.getLastName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setAge(userDTO.getAge());
        user.setGender(userDTO.getGender());
        user.setAddress(userDTO.getAddress());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAboutMe(userDTO.getAboutMe());

        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);

        userRepository.save(user);

        return user;
    }

    // login
    @Override
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return user;
    }

    @Override
    public User loginUser(String username, String password) {

        if (username.isEmpty() || password.isEmpty()) {
            throw new RuntimeException("Please enter both username and password");
        }

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("Incorrect username");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }
        return user;
    }

    // update
    @Override
    public void updateUser(String loggedInUsername, UserDTO userDTO) {

        User user = userRepository.findByUsername(loggedInUsername);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        if (!user.getUsername().equals(userDTO.getUsername()) && userRepository.findByUsername(userDTO.getUsername()) != null) {
            throw new RuntimeException("Username is already taken.");
        }

        if (!user.getEmail().equals(userDTO.getEmail()) && userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new RuntimeException("Email is already in use.");
        }

        if (userDTO.getOldPassword() != null && !userDTO.getOldPassword().isEmpty()) {
            if (!passwordEncoder.matches(userDTO.getOldPassword(), user.getPassword())) {
                throw new RuntimeException("Current password is incorrect.");
            }

            if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().isEmpty()) {
                if (!userDTO.getNewPassword().equals(userDTO.getConfirmNewPassword())) {
                    throw new RuntimeException("New passwords do not match.");
                }
                user.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
            }
        } else {
            throw new RuntimeException("You must confirm your current password.");
        }

        user.setFirstName(userDTO.getFirstName());
        user.setMiddleName(userDTO.getMiddleName());
        user.setLastName(userDTO.getLastName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setAge(userDTO.getAge());
        user.setGender(userDTO.getGender());
        user.setAddress(userDTO.getAddress());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAboutMe(userDTO.getAboutMe());

        userRepository.save(user);
    }


    // delete
    @Override
    public void deleteUser(String inputUsername, String loggedInUsername) {

        if (!inputUsername.equals(loggedInUsername)) {
            throw new RuntimeException("You can only delete your own account.");
        }

        User user = userRepository.findByUsername(inputUsername);

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        userRepository.delete(user);
    }
}