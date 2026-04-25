package edu.tcu.cs.projectpulse.instructor.report.dto;

import edu.tcu.cs.projectpulse.student.war.dto.WarResponse;

import java.util.List;

public class TeamWarReportResponse {

    private Long teamId;
    private String teamName;
    private Long sectionId;
    private String sectionName;
    private Long activeWeekId;
    private Integer weekNumber;
    private int totalSubmissions;
    private List<WarResponse> wars;

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

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

    public Long getActiveWeekId() {
        return activeWeekId;
    }

    public void setActiveWeekId(Long activeWeekId) {
        this.activeWeekId = activeWeekId;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public int getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(int totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public List<WarResponse> getWars() {
        return wars;
    }

    public void setWars(List<WarResponse> wars) {
        this.wars = wars;
    }
}
