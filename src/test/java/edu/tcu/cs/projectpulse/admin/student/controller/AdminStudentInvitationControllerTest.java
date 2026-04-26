package edu.tcu.cs.projectpulse.admin.student.controller;

import edu.tcu.cs.projectpulse.admin.student.dto.InviteStudentsResponse;
import edu.tcu.cs.projectpulse.admin.student.service.AdminStudentInvitationService;
import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.GlobalExceptionHandler;
import edu.tcu.cs.projectpulse.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminStudentInvitationController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AdminStudentInvitationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminStudentInvitationService adminStudentInvitationService;

    private InviteStudentsResponse inviteStudentsResponse;

    @BeforeEach
    void setUp() {
        inviteStudentsResponse = new InviteStudentsResponse();
        inviteStudentsResponse.setEmails(List.of("ada@tcu.edu", "grace@tcu.edu"));
        inviteStudentsResponse.setInvitedCount(2);
        inviteStudentsResponse.setSubject("You're invited to join Project Pulse");
        inviteStudentsResponse.setMessagePreview("""
                Hello,

                You've been invited to join Project Pulse.
                Use your invited email address to create your student account.

                Thanks,
                Project Pulse Admin
                """);
    }

    @Test
    @DisplayName("POST /api/students/invite returns invite preview and recipients")
    void inviteStudentsShouldReturnPreviewAndRecipients() throws Exception {
        when(adminStudentInvitationService.inviteStudents(any())).thenReturn(inviteStudentsResponse);

        String requestBody = """
                {
                  "emails": [
                    "ada@tcu.edu",
                    "grace@tcu.edu"
                  ]
                }
                """;

        mockMvc.perform(post("/api/students/invite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invitedCount").value(2))
                .andExpect(jsonPath("$.emails[0]").value("ada@tcu.edu"))
                .andExpect(jsonPath("$.subject").value("You're invited to join Project Pulse"))
                .andExpect(jsonPath("$.messagePreview").value(inviteStudentsResponse.getMessagePreview()));
    }

    @Test
    @DisplayName("POST /api/students/invite returns 400 for invalid email input")
    void inviteStudentsShouldReturnBadRequestForInvalidEmailInput() throws Exception {
        String invalidBody = """
                {
                  "emails": [
                    "not-an-email",
                    ""
                  ]
                }
                """;

        mockMvc.perform(post("/api/students/invite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$['fieldErrors']['emails[0]']").exists())
                .andExpect(jsonPath("$['fieldErrors']['emails[1]']").exists());
    }

    @Test
    @DisplayName("POST /api/students/invite returns 400 when emails repeat")
    void inviteStudentsShouldReturnBadRequestForDuplicateEmails() throws Exception {
        when(adminStudentInvitationService.inviteStudents(any()))
                .thenThrow(new BusinessRuleException("Invitation emails must be unique."));

        String requestBody = """
                {
                  "emails": [
                    "ada@tcu.edu",
                    "ADA@tcu.edu"
                  ]
                }
                """;

        mockMvc.perform(post("/api/students/invite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invitation emails must be unique."));
    }
}
