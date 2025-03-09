package com.example.presidential_elections.controller;

import com.example.presidential_elections.service.VoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vote")
public class VoteController {
    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/{candidateId}")
    public String vote(@PathVariable Long candidateId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long voterId = (Long) session.getAttribute("userId");

        String message = voteService.submitVote(voterId, candidateId);
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/user/candidacy/candidates-list";
    }

}

