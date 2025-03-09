package com.example.presidential_elections.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "user_sequence",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "voter_id", nullable = false, unique = true)
    private User voter;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidacy candidate;

    private LocalDateTime timestamp;

    public Vote() {
    }

    public Vote(Long id, User voter, Candidacy candidate, LocalDateTime timestamp) {
        this.id = id;
        this.voter = voter;
        this.candidate = candidate;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getVoter() {
        return voter;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }

    public Candidacy getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidacy candidate) {
        this.candidate = candidate;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

