package edu.tcu.cs.projectpulse.admin.team.dto;

import java.util.List;

public class TeamAssignmentResponse {

    private Long sectionId;
    private String sectionName;
    private List<TeamAssignmentTeamResponse> teams;
    private List<TeamAssignmentStudentResponse> students;

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<TeamAssignmentTeamResponse> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamAssignmentTeamResponse> teams) {
        this.teams = teams;
    }

    public List<TeamAssignmentStudentResponse> getStudents() {
        return students;
    }

    public void setStudents(List<TeamAssignmentStudentResponse> students) {
        this.students = students;
    }
}
