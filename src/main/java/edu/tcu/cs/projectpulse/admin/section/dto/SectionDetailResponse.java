package edu.tcu.cs.projectpulse.admin.section.dto;

import java.time.LocalDate;
import java.util.List;

public class SectionDetailResponse {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long rubricId;
    private String rubricName;
    private List<SectionWeekResponse> selectedWeeks;
    private List<SectionTeamResponse> teams;
    private List<SectionStudentResponse> students;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getRubricId() {
        return rubricId;
    }

    public void setRubricId(Long rubricId) {
        this.rubricId = rubricId;
    }

    public String getRubricName() {
        return rubricName;
    }

    public void setRubricName(String rubricName) {
        this.rubricName = rubricName;
    }

    public List<SectionWeekResponse> getSelectedWeeks() {
        return selectedWeeks;
    }

    public void setSelectedWeeks(List<SectionWeekResponse> selectedWeeks) {
        this.selectedWeeks = selectedWeeks;
    }

    public List<SectionTeamResponse> getTeams() {
        return teams;
    }

    public void setTeams(List<SectionTeamResponse> teams) {
        this.teams = teams;
    }

    public List<SectionStudentResponse> getStudents() {
        return students;
    }

    public void setStudents(List<SectionStudentResponse> students) {
        this.students = students;
    }
}
