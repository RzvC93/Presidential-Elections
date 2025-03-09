package com.example.presidential_elections.service;

import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.model.Vote;
import com.example.presidential_elections.repository.CandidacyRepository;
import com.example.presidential_elections.repository.UserRepository;
import com.example.presidential_elections.repository.VoteRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final CandidacyRepository candidacyRepository;
    private final UserRepository userRepository;

    public VoteService(VoteRepository voteRepository, CandidacyRepository candidacyRepository, UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.candidacyRepository = candidacyRepository;
        this.userRepository = userRepository;
    }

    public String submitVote(Long voterId, Long candidateId) {
        if (voteRepository.existsByVoterId(voterId)) {
            return "You have already voted!";
        }

        User voter = userRepository.findById(voterId)
                .orElse(null);
        Candidacy candidate = candidacyRepository.findById(candidateId)
                .orElse(null);

        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setCandidate(candidate);
        vote.setTimestamp(LocalDateTime.now());

        voteRepository.save(vote);

        return "Vote submit successfully!";
    }
}