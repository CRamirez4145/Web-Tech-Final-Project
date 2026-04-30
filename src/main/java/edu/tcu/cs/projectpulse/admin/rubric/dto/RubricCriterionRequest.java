package edu.tcu.cs.projectpulse.admin.rubric.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RubricCriterionRequest {

    @NotBlank(message = "Criterion name is required.")
    @Size(max = 150, message = "Criterion name must be 150 characters or fewer.")
    private String name;

    @Size(max = 255, message = "Criterion description must be 255 characters or fewer.")
    private String description;

    @NotNull(message = "Criterion max score is required.")
    @Min(value = 1, message = "Criterion max score must be at least 1.")
    private Integer maxScore;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }
}
