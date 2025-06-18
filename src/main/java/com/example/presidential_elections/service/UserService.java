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

    // ===================== AUTH =====================
    @Override
    public User registerUser(UserDTO userDTO) {
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new RuntimeException("❗ Oops! Passwords do not match. 🔐 Please try again.");
        }

        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("❗ The username '" + userDTO.getUsername() + "' is already in use. 🔁 Try a different one!");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("🚫 Email 📧 '" + userDTO.getEmail() + "' is already in use. Please try another one. ✉️");
        }

        if (userDTO.getAge() < 18) {
            throw new RuntimeException("🚫 Registration failed: Age must be 18 or older. 🎂🔞");
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

    @Override
    public User loginUser(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            throw new RuntimeException("🚨 Oops! Missing 🧑 username or 🔒 password. Try again!");
        }

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("❌ Incorrect 🧑 Username. Please double-check and try again!");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("🔐 Incorrect 🔑 Password. Please try again carefully!");
        }

        return user;
    }

    // ===================== PROFILE UPDATE =====================
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void updateUser(String loggedInUsername, UserDTO userDTO) {
        User user = userRepository.findByUsername(loggedInUsername);

        if (user == null) {
            throw new RuntimeException("🙅‍♂️ User not found! Please log in and try again.");
        }

        if (!user.getUsername().equals(userDTO.getUsername()) &&
                userRepository.findByUsername(userDTO.getUsername()) != null) {
            throw new RuntimeException("❗ Username '" + userDTO.getUsername() +
                    "' is already taken. Please choose another one. ✏️");
        }

        if (!user.getEmail().equals(userDTO.getEmail()) &&
                userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new RuntimeException("📧 Email '" + userDTO.getEmail() + "' is already in use. Try another one!");
        }

        if (userDTO.getOldPassword() != null && !userDTO.getOldPassword().isEmpty()) {
            if (!passwordEncoder.matches(userDTO.getOldPassword(), user.getPassword())) {
                throw new RuntimeException("🔒 Current password is incorrect. Please double-check and try again.");
            }

            if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().isEmpty()) {
                if (!userDTO.getNewPassword().equals(userDTO.getConfirmNewPassword())) {
                    throw new RuntimeException("⚠️ New passwords don’t match! Please retype them carefully. 🔁");
                }

                user.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
            }
        } else {
            throw new RuntimeException("🔐 Please confirm your current password before making changes.");
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

    // ===================== DELETE =====================
    @Override
    public void deleteUser(String inputUsername, String loggedInUsername) {
        if (!inputUsername.equals(loggedInUsername)) {
            throw new RuntimeException("⛔ You can only delete **your own account**. Unauthorized action denied! 🔐");
        }

        User user = userRepository.findByUsername(inputUsername);

        if (user == null) {
            throw new RuntimeException("🙅‍♂️ User not found! Please log in and try again.");
        }

        userRepository.delete(user);
    }

    // ===================== Additional methods =====================
    public boolean checkPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }
}