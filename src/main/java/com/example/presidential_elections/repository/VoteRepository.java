package com.example.presidential_elections.repository;

import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // ===================== VOTE EXISTENCE =====================
    boolean existsByVoterAndRoundAndPositionAndCountry(User voter, int round, String position, String country);

    // ===================== FIND VOTES =====================
    List<Vote> findByRound(int round);

    // ===================== COUNT VOTES =====================
    int countByCandidateId(Long candidateId);
    long countByCandidateAndRound(Candidacy candidate, int round);
}