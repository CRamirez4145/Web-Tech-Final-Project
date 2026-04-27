package edu.tcu.cs.projectpulse.admin.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateTeamRequest {

    @NotBlank(message = "Team name is required.")
    @Size(max = 100, message = "Team name must be 100 characters or fewer.")
    private String name;

    @Size(max = 255, message = "Team description must be 255 characters or fewer.")
    private String description;

    @Size(max = 255, message = "Team website must be 255 characters or fewer.")
    @Pattern(
            regexp = "^(https?://.+)?$",
            message = "Team website must start with http:// or https://."
    )
    private String website;

    @NotNull(message = "Section id is required.")
    private Long sectionId;

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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }
}
