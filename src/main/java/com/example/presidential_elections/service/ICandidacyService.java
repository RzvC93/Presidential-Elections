package com.example.presidential_elections.service;

import com.example.presidential_elections.model.Candidacy;
import jakarta.transaction.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ICandidacyService {

    // ===================== SUBMIT =====================
    Candidacy submitCandidacy(String username,
                              String position,
                              String description,
                              String slogan,
                              String politicalParty,
                              boolean agreedTerms,
                              String criminalRecord,
                              String arrestRecord,
                              String investigationRecord,
                              String pendingCharges,
                              String terrorismRecord,
                              String bankruptcyRecord,
                              String taxDebtRecord,
                              String disqualificationRecord,
                              String corruptionRecord,
                              String humanRightsViolation,
                              String expulsionRecord,
                              String organizedCrimeRecord,
                              String fraudRecord,
                              String legalDisputes,
                              String country,
                              MultipartFile profilePicture);

    // ===================== UPDATE =====================
    Candidacy updateCandidacy(String username, Candidacy candidacy);

    // ===================== DELETE =====================
    void withdrawCandidacy(String username, String country, String politicalParty, String position);

    // ===================== GET =====================
    List<Candidacy> getAllCandidacies();

    Candidacy findByUserUsername(String username);

    Candidacy findById(Long id);
}