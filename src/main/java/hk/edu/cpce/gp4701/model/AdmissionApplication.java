package hk.edu.cpce.gp4701.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class AdmissionApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Account member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "programme_level_id", nullable = false)
    private ProgrammeLevel programmeLevel;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "decision", length = 10)
    private String decision;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "offer_response", length = 10)
    private String offerResponse;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    public Long getId() {
        return id;
    }

    public Account getMember() {
        return member;
    }

    public void setMember(Account member) {
        this.member = member;
    }

    public ProgrammeLevel getProgrammeLevel() {
        return programmeLevel;
    }

    public void setProgrammeLevel(ProgrammeLevel programmeLevel) {
        this.programmeLevel = programmeLevel;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getOfferResponse() {
        return offerResponse;
    }

    public void setOfferResponse(String offerResponse) {
        this.offerResponse = offerResponse;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
}