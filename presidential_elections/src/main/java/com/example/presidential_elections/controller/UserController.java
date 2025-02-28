package com.example.presidential_elections.controller;

import com.example.presidential_elections.dto.UserDTO;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;


@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // signup
    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@ModelAttribute("user") UserDTO userDTO, Model model) {
        try {
            userService.registerUser(userDTO);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    // login
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password,
                            Model model, HttpSession session) {
        try {
            User user = userService.loginUser(username, password);
            session.setAttribute("loggedInUser", user.getUsername());
            return "redirect:/home";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    // update
    @GetMapping("/update")
    public String showUpdateForm(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "update";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") UserDTO userDTO, HttpSession session, Model model) {
        try {
            String loggedInUsername = (String) session.getAttribute("loggedInUser");

            userService.updateUser(loggedInUsername, userDTO);

            model.addAttribute("error", "Profile updated successfully.");

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "update";
    }

    // delete
    @GetMapping("/delete-account")
    public String showDeleteForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "delete-account";
    }

    @PostMapping("/delete-account")
    public String deleteAccount(@RequestParam String username, HttpSession session, Model model) {
        try {
            String loggedInUsername = (String) session.getAttribute("loggedInUser");

            userService.deleteUser(username, loggedInUsername);

            session.invalidate();

            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "delete-account";
        }
    }

    // home
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal User user) {
        return "home";
    }

    // about
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}