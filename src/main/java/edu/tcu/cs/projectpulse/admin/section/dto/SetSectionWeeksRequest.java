package edu.tcu.cs.projectpulse.admin.section.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class SetSectionWeeksRequest {

    @NotNull(message = "Week ids are required.")
    private List<@NotNull(message = "Week id is required.") Long> weekIds;

    public List<Long> getWeekIds() {
        return weekIds;
    }

    public void setWeekIds(List<Long> weekIds) {
        this.weekIds = weekIds;
    }
}
