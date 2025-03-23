package com.example.presidential_elections.controller;

import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.service.CandidacyService;
import com.example.presidential_elections.service.UserService;
import com.example.presidential_elections.service.VoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/user/candidacy")
public class CandidacyControler {

    private final CandidacyService candidacyService;
    private final UserService userService;
    private final VoteService voteService;

    public CandidacyControler(CandidacyService candidacyService, UserService userService, VoteService voteService) {
        this.candidacyService = candidacyService;
        this.userService = userService;
        this.voteService = voteService;
    }

    // ========== 1. Candidate Application ==========
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
                                       @RequestParam("profilePicture") MultipartFile profilePicture,
                                       HttpSession session, Model model) {

        String loggedInUsername = (String) session.getAttribute("loggedInUser");

        try {
            candidacyService.submitCandidacy(
                    loggedInUsername, position, description, slogan, politicalParty, agreedTerms,
                    criminalRecord, arrestRecord, investigationRecord, pendingCharges, terrorismRecord,
                    bankruptcyRecord, taxDebtRecord, disqualificationRecord, corruptionRecord, humanRightsViolation,
                    expulsionRecord, organizedCrimeRecord, fraudRecord, legalDisputes, profilePicture
            );

            model.addAttribute("success", "Candidacy submitted successfully!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "candidate-application";
    }

    // ========== 2. Update Candidacy ==========
    @GetMapping("/update-candidacy")
    public String showUpdateCandidacy(Model model, HttpSession session) {
        String loggedInUsername = (String) session.getAttribute("loggedInUser");
        Candidacy existingCandidacy = candidacyService.findByUserUsername(loggedInUsername);
        model.addAttribute("candidacy", existingCandidacy);
        return "update-candidacy";
    }

    @PostMapping("/update-candidacy")
    public String updateCandidacy(@ModelAttribute("candidacy") Candidacy candidacy, Model model, HttpSession session) {
        try {
            String loggedInUsername = (String) session.getAttribute("loggedInUser");
            candidacyService.updateCandidacy(loggedInUsername, candidacy);
            model.addAttribute("success", "Candidacy updated successfully!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "update-candidacy";
    }

    // ========== 3. Withdraw Candidacy ==========
    @GetMapping("/withdraw-candidacy")
    public String showWithdrawCandidacyPage() {
        return "withdraw-candidacy";
    }

    @PostMapping("/withdraw-candidacy")
    public String withdrawCandidacy(HttpSession session, Model model) {
        String loggedInUsername = (String) session.getAttribute("loggedInUser");
        try {
            candidacyService.withdrawCandidacy(loggedInUsername);
            model.addAttribute("success", "Candidacy withdrawn successfully!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "withdraw-candidacy";
    }

    // ========== 4. View All Candidates ==========
    @GetMapping("/candidates-list")
    public String showCandidateList(HttpSession session, Model model) {
        String loggedInUsername = (String) session.getAttribute("loggedInUser");

        Candidacy candidacy = candidacyService.findByUserUsername(loggedInUsername);
        if (candidacy == null) {
            model.addAttribute("error",
                    "You have not applied for candidacy yet. To apply, visit the application page.");
        }

        List<Candidacy> allCandidacies = candidacyService.getAllCandidacies();
        model.addAttribute("candidacies", allCandidacies);

        User user = userService.findByUsername(loggedInUsername);
        model.addAttribute("user", user);

        return "candidates-list";
    }

    // ========== 5. Candidate Profile ==========
    @GetMapping("/candidate-profile/{id}")
    public String showViewDetails(@PathVariable Long id, Model model) {
        Candidacy candidacy = candidacyService.findById(id);
        User user = candidacy.getUser();
        model.addAttribute("candidacy", candidacy);
        model.addAttribute("user", user);

        return "candidate-profile";
    }
}