package com.example.presidential_elections.service;

import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.model.Vote;
import com.example.presidential_elections.repository.CandidacyRepository;
import com.example.presidential_elections.repository.UserRepository;
import com.example.presidential_elections.repository.VoteRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VoteService implements IVoteService {

    private final VoteRepository voteRepository;
    private final CandidacyRepository candidacyRepository;
    private final UserRepository userRepository;

    private int currentRound = 1;
    private boolean votingOpen = false;
    private boolean voteOver = false;
    private String votingMessage = "Voting has not started yet.";

    private List<Candidacy> nextRoundCandidates;

    public VoteService(VoteRepository voteRepository,
                       CandidacyRepository candidacyRepository,
                       UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.candidacyRepository = candidacyRepository;
        this.userRepository = userRepository;
        resetVotingProcess();
    }

    // ========== 1. Get current round ==========
    public int getCurrentRound() {
        return currentRound;
    }

    // ========== 2. Check if voting is open ==========
    public boolean isVotingOpen() {
        return votingOpen;
    }

    // ========== 3. Check if voting is over ==========
    public boolean isVoteOver() {
        return voteOver;
    }

    // ========== 4. Get voting message ==========
    public String getVotingMessage() {
        return votingMessage;
    }

    // ========== 5. Get candidates for the next round ==========
    public List<Candidacy> getNextRoundCandidates() {
        if (nextRoundCandidates != null) {
            nextRoundCandidates.forEach(candidate -> {
                long count = voteRepository.countByCandidate(candidate);
                candidate.setVoteCount((int) count);
            });
        }
        return nextRoundCandidates;
    }

    // ========== 6. Update voting status ==========
    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void updateVotingStatus() {
        if (voteOver) {
            votingMessage = "Voting has ended. Thank you for participating!";
            return;
        }

        votingOpen = !votingOpen;

        if (!votingOpen) {
            votingMessage = "Voting closed for round " + currentRound + ". Evaluating results...";
            evaluateRound();
            ++currentRound;
        } else {
            votingMessage = "Voting is now open for round " + currentRound + ". Submit your votes!";
        }
    }

    // ========== 7. Evaluate the results of the round ==========
    private void evaluateRound() {
        List<Vote> votes = voteRepository.findByRound(currentRound);

        if (votes.isEmpty()) {
            votingMessage = "No votes were submitted in round " + currentRound + ". Voting will proceed to the next round.";
            nextRoundCandidates = null;
            return;
        }

        Map<Candidacy, Long> voteCount = votes.stream()
                .collect(Collectors.groupingBy(Vote::getCandidate, Collectors.counting()));

        long maxVotes = voteCount.values().stream().max(Long::compareTo).orElse(0L);

        List<Candidacy> topCandidates = voteCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((v1, v2) -> Long.compare(v2, v1)))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (topCandidates.size() == 1) {
            voteOver = true;
            Candidacy winner = topCandidates.get(0);
            votingMessage = "The winner is " + winner.getUser().getFirstName() + " " + winner.getUser().getLastName() +
                    "! Voting has ended.";
            nextRoundCandidates = null;
            return;
        }

        nextRoundCandidates = topCandidates;

        if (topCandidates.stream().allMatch(candidate -> voteCount.get(candidate).equals(maxVotes))) {
            votingMessage = "Round " + currentRound +
                    " has ended. The vote is tight, with a tie among candidates! The following " + topCandidates.size() +
                    " candidates will proceed to the next round.";
        } else if (topCandidates.size() == 2 && !voteCount.get(topCandidates.get(0)).equals(voteCount.get(topCandidates.get(1)))) {
            votingMessage = "Round " + currentRound +
                    " has ended. The top 2 candidates, with a clear vote difference, will proceed to the next round.";
        } else {
            votingMessage = "Round " + currentRound +
                    " has ended. The following " + topCandidates.size() + " candidates will proceed to the next round," +
                    " based on the votes received.";
        }

        for (Candidacy candidate : nextRoundCandidates) {
            candidate.setVoteCount(0);
            candidacyRepository.save(candidate);
        }
    }

    // ========== 8. Submit a vote ==========
    public void submitVote(Long candidateId, Long voterId, int round) {
        if (voteOver) {
            throw new RuntimeException("Voting has ended. No further votes are allowed.");
        }

        if (!votingOpen) {
            throw new RuntimeException("Voting is currently closed. Please wait for the next round.");
        }

        User voter = userRepository.findById(voterId)
                .orElseThrow(() -> new RuntimeException("You must be logged in to vote!"));

        if (voteRepository.existsByVoterAndRound(voter, round)) {
            throw new RuntimeException("You have already voted in round " + round + "!");
        }

        Candidacy candidate = candidacyRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found!"));

        Vote vote = new Vote();
        vote.setCandidate(candidate);
        vote.setVoter(voter);
        vote.setRound(round);
        vote.setTimestamp(LocalDateTime.now());

        voteRepository.save(vote);
    }

    // ========== 9. Get the winner of the election ==========
    public Candidacy getWinner() {
        if (!voteOver) {
            return null;
        }
        List<Vote> votes = voteRepository.findByRound(currentRound - 1);
        Map<Candidacy, Long> voteCount = votes.stream()
                .collect(Collectors.groupingBy(Vote::getCandidate, Collectors.counting()));
        return voteCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // ========== 10. Get all candidates with their vote counts ==========
    public List<Candidacy> getCandidatesWithVoteCounts() {
        List<Candidacy> candidates = candidacyRepository.findAll();
        candidates.forEach(candidate -> {
            long count = voteRepository.countByCandidate(candidate);
            candidate.setVoteCount((int) count);
        });

        candidates.sort((c1, c2) -> Integer.compare(c2.getVoteCount(), c1.getVoteCount()));

        return candidates;
    }

    // ========== 11. Reset the voting process ==========
    public void resetVotingProcess() {
        voteRepository.deleteAll();
        currentRound = 1;
        votingOpen = false;
        voteOver = false;
        votingMessage = "Voting has not started yet.";
        nextRoundCandidates = null;
    }
}