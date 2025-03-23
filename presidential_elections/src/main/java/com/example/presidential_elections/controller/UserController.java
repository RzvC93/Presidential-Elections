package com.example.presidential_elections.controller;

import com.example.presidential_elections.dto.UserDTO;
import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.service.CandidacyService;
import com.example.presidential_elections.service.UserService;
import com.example.presidential_elections.service.VoteService;
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
    private final VoteService voteService;

    public UserController(UserService userService, CandidacyService candidacyService, VoteService voteService) {
        this.userService = userService;
        this.candidacyService = candidacyService;
        this.voteService = voteService;
    }

    // ===================== GENERAL PAGES =====================

    @GetMapping("/home")
    public String showHomePage(Model model) {
        List<Candidacy> candidates;

        if (voteService.getNextRoundCandidates() != null) {
            candidates = voteService.getNextRoundCandidates();
        } else {
            candidates = candidacyService.getAllCandidacies();
        }

        model.addAttribute("candidacies", candidates);
        model.addAttribute("voteOver", voteService.isVoteOver());
        model.addAttribute("winner", voteService.getWinner());
        model.addAttribute("votingMessage", voteService.getVotingMessage());

        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    // ===================== AUTH =====================

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

    // ===================== PROFILE =====================

    @GetMapping("/profile")
    public String showPersonalProfile(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loggedInUser");

        User user = userService.findByUsername(username);
        Candidacy candidacy = candidacyService.findByUserUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("candidacy", candidacy);

        return "profile";
    }

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
            String username = (String) session.getAttribute("loggedInUser");

            userService.updateUser(username, userDTO);

            model.addAttribute("error", "Profile updated successfully.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "update-profile";
    }

    // ===================== DELETE ACCOUNT =====================

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
}