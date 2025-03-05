    package com.example.presidential_elections.controller;

    import com.example.presidential_elections.dto.UserDTO;
    import com.example.presidential_elections.model.Candidacy;
    import com.example.presidential_elections.model.User;
    import com.example.presidential_elections.service.CandidacyService;
    import com.example.presidential_elections.service.UserService;
    import jakarta.servlet.http.HttpSession;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.ui.Model;
    import org.springframework.web.multipart.MultipartFile;

    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.List;


    @Controller
    public class UserController {

        private final UserService userService;
        private final CandidacyService candidacyService;

        public UserController(UserService userService, CandidacyService candidacyService) {
            this.userService = userService;
            this.candidacyService = candidacyService;
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
                return "redirect:/profile";
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

        // about
        @GetMapping("/about")
        public String about() {
            return "about";
        }

        // candidate app
        @GetMapping("/candidate-application")
        public String showCandidateApplication() {
            return "candidate-application";
        }

        @PostMapping("/candidate-application")
        public String candidateApplication(@RequestParam String position,
                                           @RequestParam String description,
                                           @RequestParam String slogan,
                                           @RequestParam String politicalParty,
                                           @RequestParam boolean agreedTerms,
                                           @RequestParam String criminalRecord,
                                           @RequestParam String arrestRecord,
                                           @RequestParam String investigationRecord,
                                           @RequestParam String pendingCharges,
                                           @RequestParam String terrorismRecord,
                                           @RequestParam String bankruptcyRecord,
                                           @RequestParam String taxDebtRecord,
                                           @RequestParam String disqualificationRecord,
                                           @RequestParam String corruptionRecord,
                                           @RequestParam String humanRightsViolation,
                                           @RequestParam String expulsionRecord,
                                           @RequestParam String organizedCrimeRecord,
                                           @RequestParam String fraudRecord,
                                           @RequestParam String legalDisputes,
                                           @RequestParam String profilePicture,
                                           HttpSession session, Model model) {

            String loggedInUsername = (String) session.getAttribute("loggedInUser");

            try {
                candidacyService.submitCandidacy(loggedInUsername, position, description, slogan, politicalParty, agreedTerms,
                        criminalRecord, arrestRecord, investigationRecord, pendingCharges, terrorismRecord,
                        bankruptcyRecord, taxDebtRecord, disqualificationRecord, corruptionRecord, humanRightsViolation,
                        expulsionRecord, organizedCrimeRecord, fraudRecord, legalDisputes, profilePicture);

                model.addAttribute("error", "Candidacy submitted successfully!");
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
            }
            return "candidate-application";
        }

        // candidates list
        @GetMapping("/candidates-list")
        public String showCandidateList(HttpSession session, Model model) {
            String loggedInUsername = (String) session.getAttribute("loggedInUser");

            Candidacy candidacy = candidacyService.findByUserUsername(loggedInUsername);

            if (candidacy == null) {
                model.addAttribute("error", "You have not applied for candidacy yet. To apply for candidacy, visit the Submit your candidacy page!");
            }

            List<Candidacy> allCandidacies = candidacyService.findAllCandidates();
            model.addAttribute("candidacies", allCandidacies);

            User user = userService.findByUsername(loggedInUsername);
            model.addAttribute("user", user);

            return "candidates-list";
        }




        @GetMapping("/home")
        public String Home() {
            return "home";
        }

        @GetMapping("/logout")
        public String logout(HttpSession session) {
            session.invalidate();
            return "redirect:/home";
        }


        @GetMapping("/view-details/{id}")
        public String showViewDetails(@PathVariable Long id, Model model) {
            Candidacy candidacy = candidacyService.findById(id);
            User user = candidacy.getUser();

            model.addAttribute("candidacy", candidacy);
            model.addAttribute("user", user);

            return "view-details";
        }

    }