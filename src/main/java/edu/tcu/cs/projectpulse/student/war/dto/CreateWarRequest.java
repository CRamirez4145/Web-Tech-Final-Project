package edu.tcu.cs.projectpulse.student.war.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateWarRequest {

    @NotNull(message = "Active week id is required.")
    private Long activeWeekId;

    @Valid
    @NotEmpty(message = "At least one activity is required.")
    private List<WarActivityRequest> activities;

    public Long getActiveWeekId() {
        return activeWeekId;
    }

    public void setActiveWeekId(Long activeWeekId) {
        this.activeWeekId = activeWeekId;
    }

    public List<WarActivityRequest> getActivities() {
        return activities;
    }

    public void setActivities(List<WarActivityRequest> activities) {
        this.activities = activities;
    }
}
