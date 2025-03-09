package com.example.presidential_elections.service;

import com.example.presidential_elections.model.Candidacy;
import jakarta.transaction.Transactional;

public interface ICandidacyService {
    Candidacy findByUserUsername(String username);

    Candidacy findById(Long id);
}
