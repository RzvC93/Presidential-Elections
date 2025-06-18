package com.example.presidential_elections.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"voter_id", "round", "position", "country"})
})
public class Vote {

    @Id
    @SequenceGenerator(
            name = "vote_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "vote_sequence",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @ManyToOne  // 🔥 Trebuie @ManyToOne, nu @OneToOne
    @JoinColumn(name = "voter_id", nullable = true)
    private User voter;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = true)
    private Candidacy candidate;

    @Column(nullable = false)
    private int round;

    @Column(nullable = false)
    private String position;

    @Column(nullable = true)
    private String country;

    private LocalDateTime timestamp;


    public Vote() {
    }

    public Vote(Long id, User voter, Candidacy candidate, int round, String position, String country, LocalDateTime timestamp) {
        this.id = id;
        this.voter = voter;
        this.candidate = candidate;
        this.round = round;
        this.position = position;
        this.country = country;
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

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

