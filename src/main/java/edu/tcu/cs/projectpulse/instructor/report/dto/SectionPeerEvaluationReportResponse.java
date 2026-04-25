package edu.tcu.cs.projectpulse.instructor.report.dto;

import java.util.List;

public class SectionPeerEvaluationReportResponse {

    private Long sectionId;
    private String sectionName;
    private Long activeWeekId;
    private Integer weekNumber;
    private List<SectionPeerEvaluationStudentResponse> students;

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

    public List<SectionPeerEvaluationStudentResponse> getStudents() {
        return students;
    }

    public void setStudents(List<SectionPeerEvaluationStudentResponse> students) {
        this.students = students;
    }
}
