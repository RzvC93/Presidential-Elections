package com.example.presidential_elections.service;

import com.example.presidential_elections.model.Candidacy;
import com.example.presidential_elections.model.User;
import com.example.presidential_elections.repository.CandidacyRepository;
import com.example.presidential_elections.repository.UserRepository;
import com.example.presidential_elections.repository.VoteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CandidacyService implements ICandidacyService {

    private final CandidacyRepository candidacyRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public CandidacyService(CandidacyRepository candidacyRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.candidacyRepository = candidacyRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
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

    public void withdrawCandidacy(String inputUsername, String loggedInUsername) {

        if (!inputUsername.equals(loggedInUsername)) {
            throw new RuntimeException("You can only delete your own account.");
        }

        Candidacy candidacy = candidacyRepository.findByUserUsername(inputUsername);

        candidacyRepository.delete(candidacy);

    }

    public List<Candidacy> getAllCandidacies() {
        List<Candidacy> candidacies = candidacyRepository.findAll();
        for (Candidacy candidacy : candidacies) {
            int voteCount = voteRepository.countByCandidateId(candidacy.getId());
            candidacy.setVoteCount(voteCount);
        }
        candidacies.sort((c1, c2) -> c2.getVoteCount() - c1.getVoteCount());
        return candidacies;
    }

    @Override
    public Candidacy findByUserUsername(String username) {
        return candidacyRepository.findByUserUsername(username);
    }


    @Override
    public Candidacy findById(Long id) {
        return candidacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidacy not found with id " + id));
    }
}