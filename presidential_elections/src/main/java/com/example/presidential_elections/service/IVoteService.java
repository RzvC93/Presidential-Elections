package com.example.presidential_elections.service;

import com.example.presidential_elections.model.User;

public interface IVoteService {
    boolean hasUserVoted(User user);

    void voteForCandidate(Long candidateId, User user);
}
