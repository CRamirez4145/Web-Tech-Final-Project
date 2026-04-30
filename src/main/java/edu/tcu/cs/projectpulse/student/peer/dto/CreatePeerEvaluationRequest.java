package edu.tcu.cs.projectpulse.student.peer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreatePeerEvaluationRequest {

    @NotNull(message = "Evaluatee id is required.")
    private Long evaluateeId;

    @NotNull(message = "Active week id is required.")
    private Long activeWeekId;

    @NotNull(message = "Rubric id is required.")
    private Long rubricId;

    @Valid
    @NotEmpty(message = "At least one criterion score is required.")
    private List<PeerCriterionScoreRequest> criterionScores;

    public Long getEvaluateeId() {
        return evaluateeId;
    }

    public void setEvaluateeId(Long evaluateeId) {
        this.evaluateeId = evaluateeId;
    }

    public Long getActiveWeekId() {
        return activeWeekId;
    }

    public void setActiveWeekId(Long activeWeekId) {
        this.activeWeekId = activeWeekId;
    }

    public Long getRubricId() {
        return rubricId;
    }

    public void setRubricId(Long rubricId) {
        this.rubricId = rubricId;
    }

    public List<PeerCriterionScoreRequest> getCriterionScores() {
        return criterionScores;
    }

    public void setCriterionScores(List<PeerCriterionScoreRequest> criterionScores) {
        this.criterionScores = criterionScores;
    }
}
