package edu.tcu.cs.projectpulse.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RubricCriterionRequest {

    @NotBlank(message = "Criterion name is required.")
    @Size(max = 150, message = "Criterion name must be 150 characters or fewer.")
    private String name;

    @Size(max = 255, message = "Criterion description must be 255 characters or fewer.")
    private String description;

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
}
