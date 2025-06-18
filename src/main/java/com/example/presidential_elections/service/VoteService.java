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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VoteService implements IVoteService {

    private final VoteRepository voteRepository;
    private final CandidacyRepository candidacyRepository;
    private final UserRepository userRepository;

    private int currentRound = 1;
    private boolean votingOpen = false;
    private boolean voteOver = false;
    private String votingMessage = "🕰️ Voting has not started yet. 🕰️";

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
    @Override
    public int getCurrentRound() {
        return currentRound;
    }

    // ========== 2. Check if voting is open ==========
    @Override
    public boolean isVotingOpen() {
        return votingOpen;
    }

    // ========== 3. Check if voting is over ==========
    @Override
    public boolean isVoteOver() {
        return voteOver;
    }

    // ========== 4. Get voting message ==========
    @Override
    public String getVotingMessage() {
        return votingMessage;
    }

    // ========== 5. Get candidates for the next round ==========
    @Override
    public List<Candidacy> getNextRoundCandidates() {
        if (nextRoundCandidates != null) {
            nextRoundCandidates.forEach(candidate -> {
                long count = voteRepository.countByCandidateAndRound(candidate, currentRound);
                candidate.setVoteCount((int) count);
            });
        }
        return nextRoundCandidates;
    }

    // ========== 6. Update voting status ==========
    @Override
    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void updateVotingStatus() {
        if (voteOver) {
            votingMessage = "🏁 Voting has ended. Thank you for participating! 🎉";
            return;
        }

        votingOpen = !votingOpen;

        if (!votingOpen) {
            votingMessage = "🔒 Voting closed for round " + currentRound + ". 🔍 Evaluating results...";
            evaluateRound();
        } else {
            votingMessage = "🟢 Voting is now open for round " + currentRound + ". 🗳️ Submit your votes!";
        }
    }

    // ========== 7. Evaluate the results of the round ==========
    private Map<String, String> roundMessagesPerPosition = new LinkedHashMap<>();
    private Map<String, Candidacy> winnersPerPosition = new LinkedHashMap<>();
    private List<Map<String, Candidacy>> roundWinnersHistory = new ArrayList<>();
    private List<Map<String, String>> roundMessagesHistory = new ArrayList<>();

    @Override
    public Map<String, String> getRoundMessagesPerPosition() {
        return roundMessagesPerPosition;
    }

    @Override
    public List<Map<String, Candidacy>> getRoundWinnersHistory() {
        return roundWinnersHistory;
    }

    @Override
    public Map<String, Candidacy> getWinnersPerPosition() {
        return winnersPerPosition;
    }

    @Override
    public List<Map<String, String>> getRoundMessagesHistory() {
        return roundMessagesHistory;
    }

    private Set<String> closedPositions = new HashSet<>();

    private void evaluateRound() {
        List<Vote> votes = voteRepository.findByRound(currentRound);
        List<Candidacy> allCandidacies = candidacyRepository.findAll();
        Set<String> allPositions = allCandidacies.stream()
                .map(Candidacy::getPosition)
                .collect(Collectors.toSet());

        roundMessagesPerPosition.clear();
        Map<String, Candidacy> currentRoundWinners = new LinkedHashMap<>();
        Map<String, String> roundMessage = new LinkedHashMap<>();
        List<Candidacy> allNextRoundCandidates = new ArrayList<>();

        if (votes.isEmpty()) {
            String msg = "📬 <strong>No votes submitted for any position in round " + currentRound +
                    ".</strong> ⏭️ Skipping to next round.";
            votingMessage = msg;

            roundMessagesPerPosition.put("__NO_VOTES__", msg);
            roundMessage.put("__NO_VOTES__", msg);

            Map<String, Candidacy> emptyRound = new LinkedHashMap<>();
            emptyRound.put("__NO_VOTES__", null);
            roundWinnersHistory.add(emptyRound);
            roundMessagesHistory.add(roundMessage);

            List<String> allOpenPositions = allCandidacies.stream()
                    .map(Candidacy::getPosition)
                    .distinct()
                    .filter(pos -> !closedPositions.contains(pos))
                    .toList();

            for (String pos : allOpenPositions) {
                Vote placeholder = new Vote();
                placeholder.setRound(currentRound);
                placeholder.setTimestamp(LocalDateTime.now());
                placeholder.setPosition(pos);
                placeholder.setCandidate(null);
                placeholder.setVoter(null);
                placeholder.setCountry(null);
                voteRepository.save(placeholder);
            }

            ++currentRound;
            return;
        }

        Map<String, Map<Candidacy, Long>> voteCountsPerPosition = votes.stream()
                .collect(Collectors.groupingBy(
                        Vote::getPosition,
                        Collectors.groupingBy(Vote::getCandidate, Collectors.counting())
                ));

        for (String position : allPositions) {
            if (closedPositions.contains(position)) continue;

            Map<Candidacy, Long> voteCount = voteCountsPerPosition.get(position);

            if (voteCount == null || voteCount.isEmpty()) {
                List<Candidacy> candidates = allCandidacies.stream()
                        .filter(c -> c.getPosition().equals(position))
                        .sorted(Comparator.comparing(Candidacy::getId))
                        .toList();

                candidates.forEach(c -> {
                    c.setVoteCount(0);
                    candidacyRepository.save(c);
                });

                Vote placeholder = new Vote();
                placeholder.setRound(currentRound);
                placeholder.setTimestamp(LocalDateTime.now());
                placeholder.setPosition(position);
                placeholder.setCandidate(null);
                placeholder.setVoter(null);
                placeholder.setCountry(null);
                voteRepository.save(placeholder);

                allNextRoundCandidates.addAll(candidates);

                String msg = "📬 <strong>No votes for position " + position + ".</strong>";
                roundMessagesPerPosition.put(position, msg);
                roundMessage.put(position, msg);
                currentRoundWinners.put(position, null);
                continue;
            }

            List<Map.Entry<Candidacy, Long>> sortedVotes = voteCount.entrySet().stream()
                    .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                    .toList();

            long topVotes = sortedVotes.get(0).getValue();
            long totalWithVotes = voteCount.values().stream().filter(v -> v > 0).count();
            List<Candidacy> topCandidates = sortedVotes.stream()
                    .filter(e -> e.getValue().equals(topVotes))
                    .map(Map.Entry::getKey)
                    .toList();

            if (totalWithVotes == 1) {
                Candidacy winner = topCandidates.get(0);
                winner.setVoteCount((int) topVotes);
                winner.setSlogan("WINNER");
                candidacyRepository.save(winner);

                for (Candidacy c : allCandidacies) {
                    if (c.getPosition().equals(position) && !c.getId().equals(winner.getId())) {
                        c.setSlogan("LOSER");
                        candidacyRepository.save(c);
                    }
                }

                winnersPerPosition.put(position, winner);
                closedPositions.add(position);

                String msg = "🏆 <strong>Winner for " + position + ":</strong> <strong>" +
                        winner.getUser().getFirstName() + " " + winner.getUser().getLastName() +
                        "</strong> with <strong>" + topVotes + " votes</strong>.";
                roundMessagesPerPosition.put(position, msg);
                roundMessage.put(position, msg);
                currentRoundWinners.put(position, winner);

            } else if (topCandidates.size() > 1) {
                topCandidates.forEach(c -> {
                    c.setVoteCount(0);
                    candidacyRepository.save(c);
                });
                allNextRoundCandidates.addAll(topCandidates);

                String msg = "⚖️ <strong>Tie in " + position + ":</strong> " +
                        topCandidates.stream()
                                .map(c -> "<strong>" + c.getUser().getFirstName() + " " + c.getUser().getLastName() + "</strong>")
                                .collect(Collectors.joining(" | ")) + " ⏭️";
                roundMessagesPerPosition.put(position, msg);
                roundMessage.put(position, msg);
                currentRoundWinners.put(position, null);

            } else {
                Candidacy first = sortedVotes.get(0).getKey();
                Candidacy second = sortedVotes.get(1).getKey();
                long v1 = sortedVotes.get(0).getValue();
                long v2 = sortedVotes.get(1).getValue();

                first.setVoteCount(0);
                second.setVoteCount(0);
                candidacyRepository.save(first);
                candidacyRepository.save(second);

                allNextRoundCandidates.add(first);
                allNextRoundCandidates.add(second);

                String msg = "🔄 <strong>Top 2 in " + position + ":</strong><br>🥇 <strong>" +
                        first.getUser().getFirstName() + " " + first.getUser().getLastName() +
                        "</strong> - <strong>" + v1 + " votes</strong><br>🥈 <strong>" +
                        second.getUser().getFirstName() + " " + second.getUser().getLastName() +
                        "</strong> - <strong>" + v2 + " votes</strong> ⏭️";
                roundMessagesPerPosition.put(position, msg);
                roundMessage.put(position, msg);
                currentRoundWinners.put(position, null);
            }
        }

        roundWinnersHistory.add(new LinkedHashMap<>(currentRoundWinners));
        roundMessagesHistory.add(new LinkedHashMap<>(roundMessage));

        if (closedPositions.containsAll(allPositions)) {
            voteOver = true;
            nextRoundCandidates = null;
            votingMessage = "✅ <strong>Voting ended. All positions decided.</strong> 🏁";
        } else {
            nextRoundCandidates = allNextRoundCandidates;

            if (allNextRoundCandidates.stream().allMatch(c -> {
                long count = voteCountsPerPosition.get(c.getPosition()) != null ?
                        voteCountsPerPosition.get(c.getPosition()).getOrDefault(c, 0L) : 0L;
                return count == (voteCountsPerPosition.get(c.getPosition()) != null ?
                        voteCountsPerPosition.get(c.getPosition()).values().stream().max(Long::compareTo).orElse(0L) : 0L);
            })) {
                votingMessage = "⚖️ <strong>Round " + currentRound + " ended. Tie detected. Candidates proceed.</strong> ⏭️";
            } else if (allNextRoundCandidates.size() == 2) {
                votingMessage = "🥇🥈 <strong>Round " + currentRound + " ended. Top 2 proceed.</strong> ⏭️";
            } else {
                votingMessage = "🔄 <strong>Round " + currentRound + " ended. Next round required.</strong> ⏭️";
            }
        }

        ++currentRound;
    }


    // ========== 8. Submit a vote ==========
    public Candidacy getCandidacyById(Long id) {
        return candidacyRepository.findById(id).orElseThrow(() -> new RuntimeException("❌ Candidate not found!"));
    }

    @Override
    public void submitVote(Long candidateId, Long voterId, int round) {
        if (voteOver) {
            throw new RuntimeException("🏁 Voting has ended. ❌ No further votes are allowed. ❌");
        }

        if (!votingOpen) {
            throw new RuntimeException("❌ Voting is currently closed. ⏳ Please wait for the next round. ❌");
        }

        User voter = userRepository.findById(voterId).orElseThrow(() -> new RuntimeException("🔒 You must be logged in to vote! 🗳️"));
        Candidacy candidate = candidacyRepository.findById(candidateId).orElseThrow(() -> new RuntimeException("❌ Candidate not found! 🧑‍💼 Please check the ID."));
        String position = candidate.getPosition();
        String country = candidate.getCountry();

        if (voteRepository.existsByVoterAndRoundAndPositionAndCountry(voter, round, position, country)) {
            throw new RuntimeException("⚠️ You've already voted in round " + round + ". ⚠️");
        }

        Vote vote = new Vote();
        vote.setCandidate(candidate);
        vote.setVoter(voter);
        vote.setPosition(position);
        vote.setCountry(country);
        vote.setRound(round);
        vote.setTimestamp(LocalDateTime.now());

        voteRepository.save(vote);
    }

    // ========== 9. Get all candidates with their vote counts ==========
    @Override
    public List<Candidacy> getCandidatesWithVoteCounts() {
        List<Candidacy> candidates = candidacyRepository.findAll();

        candidates.forEach(candidate -> {
            long count = voteRepository.countByCandidateAndRound(candidate, currentRound);
            candidate.setVoteCount((int) count);
        });

        candidates.sort((c1, c2) -> Integer.compare(c2.getVoteCount(), c1.getVoteCount()));

        return candidates;
    }

    // ========== 10. Reset the voting process ==========
    @Override
    public void resetVotingProcess() {
        voteRepository.deleteAll();
        currentRound = 1;
        votingOpen = false;
        voteOver = false;
        votingMessage = "🕰️ Voting has not started yet. 🕰️";
        nextRoundCandidates = null;
        winnersPerPosition.clear();
        closedPositions.clear();
        roundWinnersHistory.clear();
        roundMessagesHistory.clear();
        roundMessagesPerPosition.clear();
    }
}