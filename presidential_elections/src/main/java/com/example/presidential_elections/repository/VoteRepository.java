package com.example.presidential_elections.repository;

import com.example.presidential_elections.model.User;
import com.example.presidential_elections.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByVoterId(Long voterId);
    int countByCandidateId(Long candidateId);
}
