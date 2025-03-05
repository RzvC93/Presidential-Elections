package com.example.presidential_elections.service;

import com.example.presidential_elections.model.Candidacy;

public interface ICandidacyService {
    Candidacy findByUserUsername(String username);
}
