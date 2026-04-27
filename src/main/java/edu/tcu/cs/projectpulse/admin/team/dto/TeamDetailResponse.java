package edu.tcu.cs.projectpulse.admin.team.dto;

import java.util.List;

public class TeamDetailResponse {

    private Long id;
    private String name;
    private String description;
    private String website;
    private Long sectionId;
    private String sectionName;
    private List<TeamStudentResponse> students;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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

    public List<TeamStudentResponse> getStudents() {
        return students;
    }

    public void setStudents(List<TeamStudentResponse> students) {
        this.students = students;
    }
}
