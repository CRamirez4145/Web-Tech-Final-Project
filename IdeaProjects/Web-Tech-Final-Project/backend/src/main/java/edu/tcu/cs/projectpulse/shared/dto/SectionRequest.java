package edu.tcu.cs.projectpulse.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SectionRequest {

    @NotBlank(message = "Section name is required.")
    @Size(max = 50, message = "Section name must be 50 characters or fewer.")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
