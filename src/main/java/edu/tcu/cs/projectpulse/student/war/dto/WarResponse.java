package edu.tcu.cs.projectpulse.student.war.dto;

import java.time.LocalDateTime;
import java.util.List;

public class WarResponse {

    private Long id;
    private Long studentId;
    private Long teamId;
    private Long sectionId;
    private Long activeWeekId;
    private Integer weekNumber;
    private LocalDateTime submittedAt;
    private String status;
    private List<WarActivityResponse> activities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<WarActivityResponse> getActivities() {
        return activities;
    }

    public void setActivities(List<WarActivityResponse> activities) {
        this.activities = activities;
    }
}
