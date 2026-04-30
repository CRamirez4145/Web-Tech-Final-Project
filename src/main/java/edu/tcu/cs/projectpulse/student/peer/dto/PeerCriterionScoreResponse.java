package edu.tcu.cs.projectpulse.student.peer.dto;

public class PeerCriterionScoreResponse {

    private Long id;
    private Long criterionId;
    private String criterionName;
    private Integer score;
    private String publicComment;
    private String privateComment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(Long criterionId) {
        this.criterionId = criterionId;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public void setCriterionName(String criterionName) {
        this.criterionName = criterionName;
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
