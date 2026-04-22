package edu.tcu.cs.projectpulse.student.peer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PeerCriterionScoreRequest {

    @NotNull(message = "Criterion id is required.")
    private Long criterionId;

    @NotNull(message = "Score is required.")
    @Min(value = 0, message = "Score cannot be negative.")
    private Integer score;

    @Size(max = 500, message = "Public comment must be 500 characters or fewer.")
    private String publicComment;

    @Size(max = 500, message = "Private comment must be 500 characters or fewer.")
    private String privateComment;

    public Long getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(Long criterionId) {
        this.criterionId = criterionId;
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
