package edu.tcu.cs.projectpulse.student.peer.entity;

import edu.tcu.cs.projectpulse.shared.entity.RubricCriterion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "peer_evaluation_criterion_scores")
public class PeerEvaluationCriterionScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "peer_evaluation_id", nullable = false)
    private PeerEvaluation peerEvaluation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "criterion_id", nullable = false)
    private RubricCriterion criterion;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 500)
    private String publicComment;

    @Column(length = 500)
    private String privateComment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PeerEvaluation getPeerEvaluation() {
        return peerEvaluation;
    }

    public void setPeerEvaluation(PeerEvaluation peerEvaluation) {
        this.peerEvaluation = peerEvaluation;
    }

    public RubricCriterion getCriterion() {
        return criterion;
    }

    public void setCriterion(RubricCriterion criterion) {
        this.criterion = criterion;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getPublicComment() {
        return publicComment;
    }

    public void setPublicComment(String publicComment) {
        this.publicComment = publicComment;
    }

    public String getPrivateComment() {
        return privateComment;
    }

    public void setPrivateComment(String privateComment) {
        this.privateComment = privateComment;
    }
}
