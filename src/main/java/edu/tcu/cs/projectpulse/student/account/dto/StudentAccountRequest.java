package edu.tcu.cs.projectpulse.student.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StudentAccountRequest {

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    @Size(max = 100, message = "Email must be 100 characters or fewer.")
    private String email;

    @NotBlank(message = "First name is required.")
    @Size(max = 100, message = "First name must be 100 characters or fewer.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 100, message = "Last name must be 100 characters or fewer.")
    private String lastName;

    @NotNull(message = "Team id is required.")
    private Long teamId;

    @NotNull(message = "Section id is required.")
    private Long sectionId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
}
