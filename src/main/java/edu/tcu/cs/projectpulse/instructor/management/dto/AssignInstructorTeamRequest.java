package edu.tcu.cs.projectpulse.instructor.management.dto;

import jakarta.validation.constraints.NotNull;

public class AssignInstructorTeamRequest {

    @NotNull(message = "Team id is required.")
    private Long teamId;

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}
