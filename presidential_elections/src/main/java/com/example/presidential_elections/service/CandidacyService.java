package com.example.presidential_elections.service;

import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.repository.CandidacyRepository;
import com.example.presidential_elections.repository.UserRepository;
import com.example.presidential_elections.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class CandidacyService implements ICandidacyService {

    private final CandidacyRepository candidacyRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public CandidacyService(CandidacyRepository candidacyRepository,
                            UserRepository userRepository,
                            VoteRepository voteRepository) {
        this.candidacyRepository = candidacyRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    // ========== 1. Submit candidacy ==========
    public Candidacy submitCandidacy(String username, String position, String description, String slogan,
                                     String politicalParty, boolean agreedTerms, String criminalRecord,
                                     String arrestRecord, String investigationRecord, String pendingCharges,
                                     String terrorismRecord, String bankruptcyRecord, String taxDebtRecord,
                                     String disqualificationRecord, String corruptionRecord, String humanRightsViolation,
                                     String expulsionRecord, String organizedCrimeRecord, String fraudRecord,
                                     String legalDisputes, MultipartFile profilePicture) {

        Candidacy existingCandidacy = candidacyRepository.findByUserUsername(username);
        if (existingCandidacy != null) {
            throw new RuntimeException("You have already applied for candidacy.");
        }

        User user = userRepository.findByUsername(username);
        String fileName = saveUploadedFile(profilePicture);

        Candidacy candidacy = new Candidacy();
        candidacy.setPosition(position);
        candidacy.setDescription(description);
        candidacy.setSlogan(slogan);
        candidacy.setPoliticalParty(politicalParty);
        candidacy.setProfilePicture(fileName);
        candidacy.setAgreedTerms(agreedTerms);
        candidacy.setCriminalRecord(criminalRecord);
        candidacy.setArrestRecord(arrestRecord);
        candidacy.setInvestigationRecord(investigationRecord);
        candidacy.setPendingCharges(pendingCharges);
        candidacy.setTerrorismRecord(terrorismRecord);
        candidacy.setBankruptcyRecord(bankruptcyRecord);
        candidacy.setTaxDebtRecord(taxDebtRecord);
        candidacy.setDisqualificationRecord(disqualificationRecord);
        candidacy.setCorruptionRecord(corruptionRecord);
        candidacy.setHumanRightsViolation(humanRightsViolation);
        candidacy.setExpulsionRecord(expulsionRecord);
        candidacy.setOrganizedCrimeRecord(organizedCrimeRecord);
        candidacy.setFraudRecord(fraudRecord);
        candidacy.setLegalDisputes(legalDisputes);
        candidacy.setUser(user);

        candidacyRepository.save(candidacy);
        return candidacy;
    }

    // ========== 2. Update candidacy ==========
    public Candidacy updateCandidacy(String username, Candidacy candidacy) {
        Candidacy existingCandidacy = candidacyRepository.findByUserUsername(username);

        if (existingCandidacy == null) {
            throw new RuntimeException("You don't have an existing candidacy to update.");
        }

        existingCandidacy.setPosition(candidacy.getPosition());
        existingCandidacy.setDescription(candidacy.getDescription());
        existingCandidacy.setSlogan(candidacy.getSlogan());
        existingCandidacy.setPoliticalParty(candidacy.getPoliticalParty());
        existingCandidacy.setProfilePicture(candidacy.getProfilePicture());
        existingCandidacy.setAgreedTerms(candidacy.isAgreedTerms());

        candidacyRepository.save(existingCandidacy);
        return existingCandidacy;
    }

    // ========== 3. Withdraw candidacy ==========
    @Transactional
    public void withdrawCandidacy(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        Candidacy candidacy = user.getCandidacy();
        if (candidacy == null) {
            throw new RuntimeException("No candidacy found for the user.");
        }

        user.setCandidacy(null);
        userRepository.save(user);
        candidacyRepository.delete(candidacy);
    }

    // ========== 4. Get all candidacies ==========
    public List<Candidacy> getAllCandidacies() {
        List<Candidacy> candidacies = candidacyRepository.findAll();

        for (Candidacy candidacy : candidacies) {
            int voteCount = voteRepository.countByCandidateId(candidacy.getId());
            candidacy.setVoteCount(voteCount);
        }

        candidacies.sort((c1, c2) -> Integer.compare(c2.getVoteCount(), c1.getVoteCount()));

        return candidacies;
    }

    // ========== 5. Find candidacy by username ==========
    @Override
    public Candidacy findByUserUsername(String username) {
        return candidacyRepository.findByUserUsername(username);
    }

    // ========== 6. Find candidacy by ID ==========
    @Override
    public Candidacy findById(Long id) {
        return candidacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidacy not found with id " + id));
    }

    // ========== Save profile picture ==========
    private String saveUploadedFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Please select a file to upload!");
        }

        try {
            String uploadDir = "uploads/";
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }
}