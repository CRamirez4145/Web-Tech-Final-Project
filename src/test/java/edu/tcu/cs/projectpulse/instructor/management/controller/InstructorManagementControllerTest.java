package edu.tcu.cs.projectpulse.instructor.management.controller;

import edu.tcu.cs.projectpulse.common.exception.GlobalExceptionHandler;
import edu.tcu.cs.projectpulse.config.SecurityConfig;
import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
import edu.tcu.cs.projectpulse.instructor.management.service.InstructorManagementService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InstructorManagementController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class InstructorManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InstructorManagementService instructorManagementService;

    private ManagedUserResponse instructorResponse;

    @BeforeEach
    void setUp() {
        instructorResponse = new ManagedUserResponse();
        instructorResponse.setId(5L);
        instructorResponse.setEmail("mentor@tcu.edu");
        instructorResponse.setFirstName("Jamie");
        instructorResponse.setLastName("Smith");
        instructorResponse.setRole("INSTRUCTOR");
        instructorResponse.setActive(false);
        instructorResponse.setSectionId(3L);
        instructorResponse.setSectionName("Section 1");
    }

    @Test
    @DisplayName("POST /api/instructor/instructors/invitations returns 201")
    void inviteInstructorShouldReturnCreated() throws Exception {
        when(instructorManagementService.inviteInstructor(any())).thenReturn(instructorResponse);

        String requestBody = """
                {
                  "email": "mentor@tcu.edu",
                  "sectionId": 3
                }
                """;

        mockMvc.perform(post("/api/instructor/instructors/invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("INSTRUCTOR"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @DisplayName("GET /api/instructor/instructors returns instructors")
    void findInstructorsShouldReturnList() throws Exception {
        when(instructorManagementService.findInstructors(null, null, null, null)).thenReturn(List.of(instructorResponse));

        mockMvc.perform(get("/api/instructor/instructors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("mentor@tcu.edu"));
    }

    @Test
    @DisplayName("PATCH /api/instructor/instructors/{id}/reactivate returns updated instructor")
    void reactivateInstructorShouldReturnUpdatedInstructor() throws Exception {
        instructorResponse.setActive(true);
        when(instructorManagementService.reactivateInstructor(eq(5L))).thenReturn(instructorResponse);

        mockMvc.perform(patch("/api/instructor/instructors/5/reactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }
}
