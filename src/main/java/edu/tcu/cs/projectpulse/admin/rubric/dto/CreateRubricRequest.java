package edu.tcu.cs.projectpulse.admin.rubric.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateRubricRequest {

    @NotBlank(message = "Rubric name is required.")
    @Size(max = 100, message = "Rubric name must be 100 characters or fewer.")
    private String name;

    @Valid
    @NotEmpty(message = "At least one criterion is required.")
    private List<RubricCriterionRequest> criteria;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RubricCriterionRequest> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<RubricCriterionRequest> criteria) {
        this.criteria = criteria;
    }
}
