package edu.tcu.cs.projectpulse.instructor.report.dto;

import edu.tcu.cs.projectpulse.student.peer.dto.PeerEvaluationResponse;

import java.math.BigDecimal;
import java.util.List;

public class StudentPeerEvaluationReportResponse {

    private Long studentId;
    private String studentName;
    private Long teamId;
    private String teamName;
    private Long sectionId;
    private String sectionName;
    private Long activeWeekId;
    private Integer weekNumber;
    private BigDecimal overallAverageScore;
    private List<PeerEvaluationResponse> evaluations;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

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

    public BigDecimal getOverallAverageScore() {
        return overallAverageScore;
    }

    public void setOverallAverageScore(BigDecimal overallAverageScore) {
        this.overallAverageScore = overallAverageScore;
    }

    public List<PeerEvaluationResponse> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<PeerEvaluationResponse> evaluations) {
        this.evaluations = evaluations;
    }
}
