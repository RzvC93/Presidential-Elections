package com.example.presidential_elections.repository;

import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // ===================== VOTE EXISTENCE =====================
    boolean existsByVoterAndRound(User voter, int round);

    boolean existsByVoterId(Long voterId);

    // ===================== FIND VOTES =====================
    List<Vote> findByCandidateAndRound(Candidacy candidate, int round);

    List<Vote> findByRound(int round);

    // ===================== COUNT VOTES =====================
    long countByCandidate(Candidacy candidate);

    int countByCandidateId(Long candidateId);
}