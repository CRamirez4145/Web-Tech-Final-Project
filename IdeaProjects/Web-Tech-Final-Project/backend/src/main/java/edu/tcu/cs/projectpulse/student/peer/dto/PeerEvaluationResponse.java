package edu.tcu.cs.projectpulse.student.peer.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PeerEvaluationResponse {

    private Long id;
    private Long evaluatorId;
    private String evaluatorName;
    private Long evaluateeId;
    private String evaluateeName;
    private Long teamId;
    private String teamName;
    private Long sectionId;
    private String sectionName;
    private Long activeWeekId;
    private Integer weekNumber;
    private Long rubricId;
    private String rubricName;
    private LocalDateTime submittedAt;
    private List<PeerCriterionScoreResponse> criterionScores;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEvaluatorId() {
        return evaluatorId;
    }

    public void setEvaluatorId(Long evaluatorId) {
        this.evaluatorId = evaluatorId;
    }

    public String getEvaluatorName() {
        return evaluatorName;
    }

    public void setEvaluatorName(String evaluatorName) {
        this.evaluatorName = evaluatorName;
    }

    public Long getEvaluateeId() {
        return evaluateeId;
    }

    public void setEvaluateeId(Long evaluateeId) {
        this.evaluateeId = evaluateeId;
    }

    public String getEvaluateeName() {
        return evaluateeName;
    }

    public void setEvaluateeName(String evaluateeName) {
        this.evaluateeName = evaluateeName;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Long getActiveWeekId() {
        return activeWeekId;
    }

    public void setActiveWeekId(Long activeWeekId) {
        this.activeWeekId = activeWeekId;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public Long getRubricId() {
        return rubricId;
    }

    public void setRubricId(Long rubricId) {
        this.rubricId = rubricId;
    }

    public String getRubricName() {
        return rubricName;
    }

    public void setRubricName(String rubricName) {
        this.rubricName = rubricName;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public List<PeerCriterionScoreResponse> getCriterionScores() {
        return criterionScores;
    }

    public void setCriterionScores(List<PeerCriterionScoreResponse> criterionScores) {
        this.criterionScores = criterionScores;
    }
}
