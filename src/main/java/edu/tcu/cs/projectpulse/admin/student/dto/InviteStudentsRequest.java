package edu.tcu.cs.projectpulse.admin.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class InviteStudentsRequest {

    @NotEmpty(message = "At least one email is required.")
    private List<
            @NotBlank(message = "Email is required.")
            @Email(message = "Email must be valid.")
            @Size(max = 100, message = "Email must be 100 characters or fewer.")
            String> emails;

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
