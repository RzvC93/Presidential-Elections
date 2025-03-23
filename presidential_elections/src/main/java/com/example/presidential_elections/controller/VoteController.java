package com.example.presidential_elections.controller;

import org.springframework.ui.Model;
import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.service.VoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/vote")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    // ===================== UPDATE VOTING STATUS =====================

    @PostMapping("/update-voting-status")
    public String updateVotingStatus() {
        voteService.updateVotingStatus();
        return "redirect:/vote/candidates-list";
    }

    // ===================== SHOW CANDIDATE LIST =====================

    @GetMapping("/candidates-list")
    public String showCandidateList(Model model) {
        model.addAttribute("votingMessage", voteService.getVotingMessage());
        model.addAttribute("votingOpen", voteService.isVotingOpen());
        model.addAttribute("currentRound", voteService.getCurrentRound());
        model.addAttribute("voteOver", voteService.isVoteOver());
        model.addAttribute("winner", voteService.getWinner());
        model.addAttribute("nextRoundCandidates", voteService.getNextRoundCandidates());

        List<Candidacy> sortedCandidates = voteService.getCandidatesWithVoteCounts();
        model.addAttribute("candidacies", sortedCandidates);

        return "candidates-list";
    }

    // ===================== SUBMIT VOTE =====================

    @PostMapping("/{candidateId}")
    public String submitVote(@PathVariable Long candidateId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long voterId = (Long) session.getAttribute("userId");

        if (voterId == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to vote!");
            return "redirect:/vote/candidates-list";
        }

        try {
            voteService.submitVote(candidateId, voterId, voteService.getCurrentRound());
            redirectAttributes.addFlashAttribute("message", "Vote successfully submitted!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/vote/candidates-list";
    }
}