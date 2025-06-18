package com.example.presidential_elections.model;


import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidacy")
public class Candidacy {
    @Id
    @SequenceGenerator(
            name = "candidacy_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "candidacy_sequence",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    private String country;
    private String position;

    @Column(columnDefinition = "TEXT", length = 300)
    private String description;
    private String slogan;
    private String politicalParty;
    private String profilePicture;
    private boolean agreedTerms;
    private String criminalRecord;
    private String arrestRecord;
    private String investigationRecord;
    private String pendingCharges;
    private String terrorismRecord;
    private String bankruptcyRecord;
    private String taxDebtRecord;
    private String disqualificationRecord;
    private String corruptionRecord;
    private String humanRightsViolation;
    private String expulsionRecord;
    private String organizedCrimeRecord;
    private String fraudRecord;
    private String legalDisputes;

    @Transient
    private int voteCount;

    @Transient
    private MultipartFile profilePictureFile;

    public Candidacy() {
    }

    public Candidacy(Long id, String country, User user, String position, String description, String slogan, String politicalParty,
                     String profilePicture, boolean agreedTerms, String criminalRecord, String arrestRecord,
                     String investigationRecord, String pendingCharges, String terrorismRecord,
                     String bankruptcyRecord, String taxDebtRecord, String disqualificationRecord,
                     String corruptionRecord, String humanRightsViolation, String expulsionRecord,
                     String organizedCrimeRecord, String fraudRecord, String legalDisputes, int voteCount) {
        this.id = id;
        this.country = country;
        this.user = user;
        this.position = position;
        this.description = description;
        this.slogan = slogan;
        this.politicalParty = politicalParty;
        this.profilePicture = profilePicture;
        this.agreedTerms = agreedTerms;
        this.criminalRecord = criminalRecord;
        this.arrestRecord = arrestRecord;
        this.investigationRecord = investigationRecord;
        this.pendingCharges = pendingCharges;
        this.terrorismRecord = terrorismRecord;
        this.bankruptcyRecord = bankruptcyRecord;
        this.taxDebtRecord = taxDebtRecord;
        this.disqualificationRecord = disqualificationRecord;
        this.corruptionRecord = corruptionRecord;
        this.humanRightsViolation = humanRightsViolation;
        this.expulsionRecord = expulsionRecord;
        this.organizedCrimeRecord = organizedCrimeRecord;
        this.fraudRecord = fraudRecord;
        this.legalDisputes = legalDisputes;
        this.voteCount = voteCount;
    }

    public Long getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getPoliticalParty() {
        return politicalParty;
    }

    public void setPoliticalParty(String politicalParty) {
        this.politicalParty = politicalParty;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public boolean isAgreedTerms() {
        return agreedTerms;
    }

    public void setAgreedTerms(boolean agreedTerms) {
        this.agreedTerms = agreedTerms;
    }

    public String getCriminalRecord() {
        return criminalRecord;
    }

    public void setCriminalRecord(String criminalRecord) {
        this.criminalRecord = criminalRecord;
    }

    public String getArrestRecord() {
        return arrestRecord;
    }

    public void setArrestRecord(String arrestRecord) {
        this.arrestRecord = arrestRecord;
    }

    public String getInvestigationRecord() {
        return investigationRecord;
    }

    public void setInvestigationRecord(String investigationRecord) {
        this.investigationRecord = investigationRecord;
    }

    public String getPendingCharges() {
        return pendingCharges;
    }

    public void setPendingCharges(String pendingCharges) {
        this.pendingCharges = pendingCharges;
    }

    public String getTerrorismRecord() {
        return terrorismRecord;
    }

    public void setTerrorismRecord(String terrorismRecord) {
        this.terrorismRecord = terrorismRecord;
    }

    public String getBankruptcyRecord() {
        return bankruptcyRecord;
    }

    public void setBankruptcyRecord(String bankruptcyRecord) {
        this.bankruptcyRecord = bankruptcyRecord;
    }

    public String getTaxDebtRecord() {
        return taxDebtRecord;
    }

    public void setTaxDebtRecord(String taxDebtRecord) {
        this.taxDebtRecord = taxDebtRecord;
    }

    public String getDisqualificationRecord() {
        return disqualificationRecord;
    }

    public void setDisqualificationRecord(String disqualificationRecord) {
        this.disqualificationRecord = disqualificationRecord;
    }

    public String getCorruptionRecord() {
        return corruptionRecord;
    }

    public void setCorruptionRecord(String corruptionRecord) {
        this.corruptionRecord = corruptionRecord;
    }

    public String getHumanRightsViolation() {
        return humanRightsViolation;
    }

    public void setHumanRightsViolation(String humanRightsViolation) {
        this.humanRightsViolation = humanRightsViolation;
    }

    public String getExpulsionRecord() {
        return expulsionRecord;
    }

    public void setExpulsionRecord(String expulsionRecord) {
        this.expulsionRecord = expulsionRecord;
    }

    public String getOrganizedCrimeRecord() {
        return organizedCrimeRecord;
    }

    public void setOrganizedCrimeRecord(String organizedCrimeRecord) {
        this.organizedCrimeRecord = organizedCrimeRecord;
    }

    public String getFraudRecord() {
        return fraudRecord;
    }

    public void setFraudRecord(String fraudRecord) {
        this.fraudRecord = fraudRecord;
    }

    public String getLegalDisputes() {
        return legalDisputes;
    }

    public void setLegalDisputes(String legalDisputes) {
        this.legalDisputes = legalDisputes;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public MultipartFile getProfilePictureFile() {
        return profilePictureFile;
    }

    public void setProfilePictureFile(MultipartFile profilePictureFile) {
        this.profilePictureFile = profilePictureFile;
    }
}
