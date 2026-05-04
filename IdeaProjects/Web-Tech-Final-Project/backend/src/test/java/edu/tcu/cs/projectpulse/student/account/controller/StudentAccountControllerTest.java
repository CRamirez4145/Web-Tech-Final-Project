package edu.tcu.cs.projectpulse.student.account.controller;

import edu.tcu.cs.projectpulse.common.exception.GlobalExceptionHandler;
import edu.tcu.cs.projectpulse.config.SecurityConfig;
import edu.tcu.cs.projectpulse.student.account.dto.StudentAccountResponse;
import edu.tcu.cs.projectpulse.student.account.service.StudentAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentAccountController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class StudentAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentAccountService studentAccountService;

    private StudentAccountResponse accountResponse;

    @BeforeEach
    void setUp() {
        accountResponse = new StudentAccountResponse();
        accountResponse.setId(1L);
        accountResponse.setEmail("javier@tcu.edu");
        accountResponse.setFirstName("Javier");
        accountResponse.setLastName("Lopez");
        accountResponse.setTeamId(2L);
        accountResponse.setTeamName("Team Alpha");
        accountResponse.setSectionId(3L);
        accountResponse.setSectionName("Section 1");
    }

    @Test
    @DisplayName("POST /api/student/account returns 201 when account is created")
    void createAccountShouldReturnCreated() throws Exception {
        when(studentAccountService.createAccount(any())).thenReturn(accountResponse);

        String requestBody = """
                {
                  "email": "javier@tcu.edu",
                  "firstName": "Javier",
                  "lastName": "Lopez",
                  "teamId": 2,
                  "sectionId": 3
                }
                """;

        mockMvc.perform(post("/api/student/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.teamName").value("Team Alpha"));
    }

    @Test
    @DisplayName("GET /api/student/account returns the current student account")
    void getMyAccountShouldReturnAccount() throws Exception {
        when(studentAccountService.getAccount(1L)).thenReturn(accountResponse);

        mockMvc.perform(get("/api/student/account")
                        .header("X-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("javier@tcu.edu"))
                .andExpect(jsonPath("$.sectionName").value("Section 1"));
    }

    @Test
    @DisplayName("PUT /api/student/account returns updated account")
    void updateMyAccountShouldReturnUpdatedAccount() throws Exception {
        when(studentAccountService.updateAccount(eq(1L), any())).thenReturn(accountResponse);

        String requestBody = """
                {
                  "email": "javier@tcu.edu",
                  "firstName": "Javier",
                  "lastName": "Lopez",
                  "teamId": 2,
                  "sectionId": 3
                }
                """;

        mockMvc.perform(put("/api/student/account")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/student/account returns 400 for invalid payload")
    void updateMyAccountShouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidBody = """
                {
                  "email": "not-an-email",
                  "firstName": "",
                  "lastName": "",
                  "teamId": null,
                  "sectionId": null
                }
                """;

        mockMvc.perform(put("/api/student/account")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.firstName").exists())
                .andExpect(jsonPath("$.fieldErrors.teamId").exists());
    }
}
