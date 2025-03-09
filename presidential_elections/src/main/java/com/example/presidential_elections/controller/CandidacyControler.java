package com.example.presidential_elections.controller;

import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.service.CandidacyService;
import com.example.presidential_elections.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user/candidacy")
public class CandidacyControler {

    private final CandidacyService candidacyService;
    private final UserService userService;

    public CandidacyControler(CandidacyService candidacyService, UserService userService) {
        this.candidacyService = candidacyService;
        this.userService = userService;
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

    // update candidacy
    @GetMapping("/update-candidacy")
    public String showUpdateCandidacy(Model model, HttpSession session) {
        String loggedInUsername = (String) session.getAttribute("loggedInUser");

        Candidacy existingCandidacy = candidacyService.findByUserUsername(loggedInUsername);

        model.addAttribute("candidacy", existingCandidacy);
        return "update-candidacy";
    }


    @PostMapping("/update-candidacy")
    public String updateCandidacy(@ModelAttribute ("candidacy") Candidacy candidacy, Model model, HttpSession session) {

        try {
            String loggedInUsername = (String) session.getAttribute("loggedInUser");

            candidacyService.updateCandidacy(loggedInUsername, candidacy);

            model.addAttribute("error", "Candidacy updated successfully!");

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "update-candidacy";
    }













    // withdraw candidacy
    @GetMapping("/withdraw-candidacy")
    public String showWithdrawForm(Model model) {
        model.addAttribute("candidacy", new Candidacy());
        return "withdraw-candidacy";
    }

    @PostMapping("/withdraw-candidacy")
    public String withdrawCandidacy(@RequestParam String username, HttpSession session, Model model) {
        try {
            String loggedInUsername = (String) session.getAttribute("loggedInUser");

            candidacyService.withdrawCandidacy(username ,loggedInUsername);
            return "redirect:/user/candidacy/candidates-list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "withdraw-candidacy";
        }
    }














    // candidates list
    @GetMapping("/candidates-list")
    public String showCandidateList(HttpSession session, Model model) {
        String loggedInUsername = (String) session.getAttribute("loggedInUser");

        Candidacy candidacy = candidacyService.findByUserUsername(loggedInUsername);

        if (candidacy == null) {
            model.addAttribute("error", "You have not applied for candidacy yet." +
                    " To apply for candidacy, visit the Submit your candidacy page!");
        }

        List<Candidacy> allCandidacies = candidacyService.getAllCandidacies();
        model.addAttribute("candidacies", allCandidacies);

        User user = userService.findByUsername(loggedInUsername);
        model.addAttribute("user", user);

        return "candidates-list";
    }

    // candidates profile
    @GetMapping("/candidate-profile/{id}")
    public String showViewDetails(@PathVariable Long id, Model model) {
        Candidacy candidacy = candidacyService.findById(id);
        User user = candidacy.getUser();

        model.addAttribute("candidacy", candidacy);
        model.addAttribute("user", user);

        return "candidate-profile";
    }
}