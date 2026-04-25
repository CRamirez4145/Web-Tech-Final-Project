package edu.tcu.cs.projectpulse.instructor.report.dto;

import java.math.BigDecimal;

public class SectionPeerEvaluationStudentResponse {

    private Long studentId;
    private String studentName;
    private Long teamId;
    private String teamName;
    private BigDecimal averageScore;
    private long evaluationCount;

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

    public BigDecimal getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(BigDecimal averageScore) {
        this.averageScore = averageScore;
    }

    public long getEvaluationCount() {
        return evaluationCount;
    }

    public void setEvaluationCount(long evaluationCount) {
        this.evaluationCount = evaluationCount;
    }
}
