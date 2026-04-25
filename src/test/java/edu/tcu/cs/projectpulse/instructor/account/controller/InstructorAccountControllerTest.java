package edu.tcu.cs.projectpulse.instructor.account.controller;

import edu.tcu.cs.projectpulse.common.exception.GlobalExceptionHandler;
import edu.tcu.cs.projectpulse.config.SecurityConfig;
import edu.tcu.cs.projectpulse.instructor.account.service.InstructorAccountService;
import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InstructorAccountController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class InstructorAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InstructorAccountService instructorAccountService;

    private ManagedUserResponse accountResponse;

    @BeforeEach
    void setUp() {
        accountResponse = new ManagedUserResponse();
        accountResponse.setId(5L);
        accountResponse.setEmail("mentor@tcu.edu");
        accountResponse.setFirstName("Jamie");
        accountResponse.setLastName("Smith");
        accountResponse.setRole("INSTRUCTOR");
        accountResponse.setActive(true);
        accountResponse.setSectionId(3L);
        accountResponse.setSectionName("Section 1");
    }

    @Test
    @DisplayName("POST /api/instructor/account returns 201")
    void createAccountShouldReturnCreated() throws Exception {
        when(instructorAccountService.createAccount(any())).thenReturn(accountResponse);

        String requestBody = """
                {
                  "email": "mentor@tcu.edu",
                  "firstName": "Jamie",
                  "lastName": "Smith"
                }
                """;

        mockMvc.perform(post("/api/instructor/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.firstName").value("Jamie"));
    }
}
