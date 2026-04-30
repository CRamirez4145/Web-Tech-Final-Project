package edu.tcu.cs.projectpulse.admin.team.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AssignStudentsRequest {

    @NotNull(message = "Team id is required.")
    private Long teamId;

    @NotEmpty(message = "At least one student id is required.")
    private List<@NotNull(message = "Student id is required.") Long> studentIds;

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }
}
