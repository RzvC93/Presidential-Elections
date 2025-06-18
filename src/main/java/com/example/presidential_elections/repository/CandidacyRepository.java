package com.example.presidential_elections.repository;

import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidacyRepository extends JpaRepository<Candidacy, Long> {

    // ===================== FIND CANDIDACY =====================
    Candidacy findByUserUsername(String username);
}