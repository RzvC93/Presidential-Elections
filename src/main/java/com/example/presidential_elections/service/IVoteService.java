package com.example.presidential_elections.service;

import com.example.presidential_elections.model.Candidacy;

import java.util.List;
import java.util.Map;

public interface IVoteService {

    // ===================== Voting Status =====================
    int getCurrentRound();

    boolean isVotingOpen();

    boolean isVoteOver();

    String getVotingMessage();

    List<Candidacy> getNextRoundCandidates();

    // ===================== Voting Process =====================
    void updateVotingStatus();

    void submitVote(Long candidateId, Long voterId, int round);

    List<Candidacy> getCandidatesWithVoteCounts();

    // ===================== Round Evaluation =====================
    Map<String, String> getRoundMessagesPerPosition();

    List<Map<String, Candidacy>> getRoundWinnersHistory();

    Map<String, Candidacy> getWinnersPerPosition();

    List<Map<String, String>> getRoundMessagesHistory();

    // ===================== Reset =====================
    void resetVotingProcess();
}