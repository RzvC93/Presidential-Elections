package com.example.presidential_elections.controller;

import com.example.presidential_elections.dto.UserDTO;
import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.service.CandidacyService;
import com.example.presidential_elections.service.UserService;
import com.example.presidential_elections.service.VoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String showHomePage(Model model, HttpSession session) {
        model.addAttribute("isLoggedIn", session.getAttribute("loggedInUser") != null);
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
    public String registerUser(@ModelAttribute("user") UserDTO userDTO,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            userService.registerUser(userDTO);

            redirectAttributes.addFlashAttribute("success",
                    "🎉 Account created successfully! You can now log in. 🚀");

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
                            HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.loginUser(username, password);
            session.setAttribute("loggedInUser", user.getUsername());
            session.setAttribute("userId", user.getId());
            redirectAttributes.addFlashAttribute("success", "✅ Login successful! Welcome, " +
                    user.getUsername() + "!");
            return "redirect:/user/home";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/login";
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

            model.addAttribute("success", "✅ Profile updated successfully! 🎉 You're all set. 🗳️");
        } catch (RuntimeException e) {
            model.addAttribute("error", "❌ " + e.getMessage() + " Please try again. 🙁");
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
    public String deleteAccount(@RequestParam String username, HttpSession session,
                                RedirectAttributes redirectAttributes, Model model) {
        try {
            String loggedInUsername = (String) session.getAttribute("loggedInUser");

            userService.deleteUser(username, loggedInUsername);
            session.invalidate();

            redirectAttributes.addFlashAttribute("success",
                    "✅ Account deleted successfully. We're sorry to see you go. 👋");
            return "redirect:/user/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "delete-account";
        }
    }
}