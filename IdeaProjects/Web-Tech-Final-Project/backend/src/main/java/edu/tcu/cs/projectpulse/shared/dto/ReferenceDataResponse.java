package edu.tcu.cs.projectpulse.shared.dto;

import java.util.List;

public class ReferenceDataResponse {

    private List<SectionResponse> sections;
    private List<TeamResponse> teams;
    private List<UserResponse> users;
    private List<RubricResponse> rubrics;
    private List<ActiveWeekResponse> activeWeeks;

    public List<SectionResponse> getSections() {
        return sections;
    }

    public void setSections(List<SectionResponse> sections) {
        this.sections = sections;
    }

    public List<TeamResponse> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamResponse> teams) {
        this.teams = teams;
    }

    public List<UserResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponse> users) {
        this.users = users;
    }

    public List<RubricResponse> getRubrics() {
        return rubrics;
    }

    public void setRubrics(List<RubricResponse> rubrics) {
        this.rubrics = rubrics;
    }

    public List<ActiveWeekResponse> getActiveWeeks() {
        return activeWeeks;
    }

    public void setActiveWeeks(List<ActiveWeekResponse> activeWeeks) {
        this.activeWeeks = activeWeeks;
    }
}
