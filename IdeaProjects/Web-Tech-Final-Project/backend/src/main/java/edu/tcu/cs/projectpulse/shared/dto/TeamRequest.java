package edu.tcu.cs.projectpulse.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TeamRequest {

    @NotBlank(message = "Team name is required.")
    @Size(max = 100, message = "Team name must be 100 characters or fewer.")
    private String name;

    @NotNull(message = "Section id is required.")
    private Long sectionId;

    private Long instructorId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }
}
