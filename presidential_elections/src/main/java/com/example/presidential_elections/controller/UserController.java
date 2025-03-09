package com.example.presidential_elections.controller;

import com.example.presidential_elections.dto.UserDTO;
import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.service.CandidacyService;
import com.example.presidential_elections.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CandidacyService candidacyService;

    public UserController(UserService userService, CandidacyService candidacyService) {
        this.userService = userService;
        this.candidacyService = candidacyService;
    }

    // home
    @GetMapping("/home")
    public String showHomePage(Model model) {
        List<Candidacy> candidacies = candidacyService.getAllCandidacies();
        model.addAttribute("candidacies", candidacies);
        return "home";
    }

    // about
    @GetMapping("/about")
    public String about() {
        return "about";
    }

    // profile
    @GetMapping("/profile")
    public String showPersonalProfile(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loggedInUser");
        String loggedInUsername = (String) session.getAttribute("loggedInUser");

        User user = userService.findByUsername(username);
        Candidacy candidacy = candidacyService.findByUserUsername(loggedInUsername);

        model.addAttribute("user", user);
        model.addAttribute("candidacy", candidacy);

        return "profile";
    }

    // update
    @GetMapping("/update-profile")
    public String showUpdateForm(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "update-profile";
    }

    @PostMapping("/update-profile")
    public String updateUser(@ModelAttribute("user") UserDTO userDTO, HttpSession session, Model model) {
        try {
            String loggedInUsername = (String) session.getAttribute("loggedInUser");

            userService.updateUser(loggedInUsername, userDTO);

            model.addAttribute("error", "Profile updated successfully.");

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "update-profile";
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

            return "redirect:/user/login";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "delete-account";
        }
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
            return "redirect:/user/login";
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
            session.setAttribute("userId", user.getId());
            return "redirect:/user/profile";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/home";
    }
}