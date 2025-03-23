package com.example.presidential_elections.service;

import com.example.presidential_elections.model.Candidacy;

import java.util.List;

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

    Candidacy getWinner();

    List<Candidacy> getCandidatesWithVoteCounts();

    // ===================== Reset =====================
    void resetVotingProcess();
}