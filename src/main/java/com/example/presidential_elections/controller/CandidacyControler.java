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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                       @RequestParam String country,
                                       @RequestParam("profilePicture") MultipartFile profilePicture,
                                       HttpSession session, Model model) {

        String loggedInUsername = (String) session.getAttribute("loggedInUser");

        try {
            candidacyService.submitCandidacy(
                    loggedInUsername, position, description, slogan, politicalParty, agreedTerms,
                    criminalRecord, arrestRecord, investigationRecord, pendingCharges, terrorismRecord,
                    bankruptcyRecord, taxDebtRecord, disqualificationRecord, corruptionRecord, humanRightsViolation,
                    expulsionRecord, organizedCrimeRecord, fraudRecord, legalDisputes, country, profilePicture
            );

            model.addAttribute("success",
                    "✅ Candidacy submitted successfully! 🎉 You are now officially in the race! 🗳️🏁");
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

        if (existingCandidacy == null) {
            model.addAttribute("permanentError",
                    "You are not a candidate! " +
                            "<a href='/user/candidacy/candidate-application' class='alert-link'>Apply here</a>.");

            Candidacy empty = new Candidacy();
            empty.setPosition("");
            empty.setPoliticalParty("");
            empty.setDescription("");
            empty.setSlogan("");
            empty.setAgreedTerms(false);
            empty.setProfilePicture(null);

            model.addAttribute("candidacy", empty);
        } else {
            model.addAttribute("candidacy", existingCandidacy);
        }

        return "update-candidacy";
    }

    @PostMapping("/update-candidacy")
    public String updateCandidacy(@ModelAttribute("candidacy") Candidacy candidacy,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  Model model, HttpSession session) {
        String loggedInUsername = (String) session.getAttribute("loggedInUser");

        try {
            if (confirmPassword == null || confirmPassword.isEmpty()) {
                model.addAttribute("error",
                        "🔐 Please confirm your password to proceed. It's required for security reasons! 🛡️");
                return "update-candidacy";
            }

            User user = userService.findByUsername(loggedInUsername);
            if (user == null || !userService.checkPassword(user, confirmPassword)) {
                model.addAttribute("error",
                        "❌ Incorrect password! Please double-check and try again. 🔐");
                return "update-candidacy";
            }

            MultipartFile file = candidacy.getProfilePictureFile();
            if (file != null && !file.isEmpty()) {
                candidacy.setProfilePicture(file.getOriginalFilename());
            }

            candidacyService.updateCandidacy(loggedInUsername, candidacy);

            model.addAttribute("success",
                    "✅ Candidacy updated successfully! 🎉 Your changes have been saved.");

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "update-candidacy";
    }

    // ========== 3. Withdraw Candidacy ==========
    @GetMapping("/withdraw-candidacy")
    public String showWithdrawCandidacyPage(HttpSession session, Model model) {
        String loggedInUsername = (String) session.getAttribute("loggedInUser");
        Candidacy candidacy = candidacyService.findByUserUsername(loggedInUsername);

        if (candidacy == null) {
            model.addAttribute("permanentError", true);
            return "withdraw-candidacy";
        }

        model.addAttribute("candidacy", candidacy);
        return "withdraw-candidacy";
    }

    @PostMapping("/withdraw-candidacy")
    public String withdrawCandidacy(@RequestParam String country,
                                    @RequestParam String position,
                                    @RequestParam String politicalParty,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        String loggedInUsername = (String) session.getAttribute("loggedInUser");

        try {
            candidacyService.withdrawCandidacy(loggedInUsername, country, politicalParty, position);

            redirectAttributes.addFlashAttribute("success",
                    "🚫 Candidacy withdrawn successfully! 🗳️ You are no longer in the race.");
            return "redirect:/user/home";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/candidacy/withdraw-candidacy";
        }
    }


    // ========== 4. View All Candidates ==========
    @GetMapping("/candidates-list")
    public String showCandidateList(HttpSession session, Model model) {
        String loggedInUsername = (String) session.getAttribute("loggedInUser");

        Candidacy candidacy = candidacyService.findByUserUsername(loggedInUsername);
        if (candidacy == null) {
            model.addAttribute("error",
                    "📭 You haven't applied for candidacy yet. " +
                            "👉 <a href='/user/candidacy/candidate-application' class='alert-link'>Apply here</a> " +
                            "to join the race! 🗳️");
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