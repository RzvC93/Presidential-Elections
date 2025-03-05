package com.example.presidential_elections.service;

import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.repository.CandidacyRepository;
import com.example.presidential_elections.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidacyService implements ICandidacyService {

    private final CandidacyRepository candidacyRepository;
    private final UserRepository userRepository;

    public CandidacyService(CandidacyRepository candidacyRepository, UserRepository userRepository) {
        this.candidacyRepository = candidacyRepository;
        this.userRepository = userRepository;
    }


    public Candidacy submitCandidacy(String username, String position, String description, String slogan, String politicalParty,
                                     boolean agreedTerms, String criminalRecord, String arrestRecord,
                                     String investigationRecord, String pendingCharges, String terrorismRecord,
                                     String bankruptcyRecord, String taxDebtRecord, String disqualificationRecord,
                                     String corruptionRecord, String humanRightsViolation, String expulsionRecord,
                                     String organizedCrimeRecord, String fraudRecord, String legalDisputes,
                                     String profilePicture) {
        Candidacy existingCandidacy = candidacyRepository.findByUserUsername(username);

        if (existingCandidacy != null) {
            throw new RuntimeException("You have already applied for candidacy.");
        }


        User user = userRepository.findByUsername(username);

        Candidacy candidacy = new Candidacy();
        candidacy.setPosition(position);
        candidacy.setDescription(description);
        candidacy.setSlogan(slogan);
        candidacy.setPoliticalParty(politicalParty);
        candidacy.setProfilePicture(profilePicture);
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


    @Override
    public Candidacy findByUserUsername(String username) {
        return candidacyRepository.findByUserUsername(username);
    }

    public List<Candidacy> findAllCandidates() {
        return candidacyRepository.findAll();
    }

    public Candidacy findById(Long id) {
        return candidacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidacy not found with id " + id));
    }
}
