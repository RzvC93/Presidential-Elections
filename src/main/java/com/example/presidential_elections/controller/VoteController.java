package com.example.presidential_elections.controller;

import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.service.VoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        model.addAttribute("winners", voteService.getWinnersPerPosition());
        model.addAttribute("roundMessagesPerPosition", voteService.getRoundMessagesPerPosition());
        model.addAttribute("roundWinnersHistory", voteService.getRoundWinnersHistory());
        model.addAttribute("roundMessagesHistory", voteService.getRoundMessagesHistory());

        List<Candidacy> sortedCandidates = voteService.getCandidatesWithVoteCounts();
        model.addAttribute("candidacies", sortedCandidates);

        List<Candidacy> relevantCandidates = voteService.getNextRoundCandidates() != null
                ? voteService.getNextRoundCandidates()
                : sortedCandidates;

        Map<String, List<Candidacy>> candidatesByPosition = relevantCandidates.stream()
                .collect(Collectors.groupingBy(Candidacy::getPosition, LinkedHashMap::new, Collectors.toList()));

        candidatesByPosition.entrySet().forEach(entry ->
                entry.setValue(
                        entry.getValue().stream()
                                .sorted(Comparator.comparingInt(Candidacy::getVoteCount).reversed())
                                .collect(Collectors.toList())
                )
        );

        if (voteService.getNextRoundCandidates() != null) {
            model.addAttribute("nextRoundCandidatesByPosition", candidatesByPosition);
        } else {
            model.addAttribute("candidatesByPosition", candidatesByPosition);
        }

        Map<String, String> positionIcons = new LinkedHashMap<>();
        positionIcons.put("President", "👑");
        positionIcons.put("Mayor", "🏛️");
        positionIcons.put("Counselor", "🗣️");
        positionIcons.put("Other", "✨");

        model.addAttribute("positionIcons", positionIcons);

        return "candidates-list";
    }

    // ===================== SUBMIT VOTE =====================
    @PostMapping("/{candidateId}")
    public String submitVote(@PathVariable Long candidateId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long voterId = (Long) session.getAttribute("userId");

        try {
            Candidacy candidate = voteService.getCandidacyById(candidateId);

            voteService.submitVote(candidateId, voterId, voteService.getCurrentRound());

            redirectAttributes.addFlashAttribute("message",
                    "✅ Vote submitted successfully! 🎉 Thanks for participating! 🎉");

            redirectAttributes.addFlashAttribute("messagePosition", candidate.getPosition());
            redirectAttributes.addFlashAttribute("selectedPosition", candidate.getPosition());

        } catch (RuntimeException e) {
            Candidacy candidate = voteService.getCandidacyById(candidateId);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("errorPosition", candidate.getPosition());
            redirectAttributes.addFlashAttribute("selectedPosition", candidate.getPosition());
        }

        return "redirect:/vote/candidates-list";
    }
}