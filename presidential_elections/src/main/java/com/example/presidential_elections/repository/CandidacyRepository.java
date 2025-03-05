package com.example.presidential_elections.repository;

import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidacyRepository extends JpaRepository<Candidacy, Long> {
    Candidacy findByUserUsername(String username);
}
