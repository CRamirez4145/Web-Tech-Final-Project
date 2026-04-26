package edu.tcu.cs.projectpulse.admin.rubric.controller;

import edu.tcu.cs.projectpulse.admin.rubric.dto.RubricCriterionResponse;
import edu.tcu.cs.projectpulse.admin.rubric.dto.RubricResponse;
import edu.tcu.cs.projectpulse.admin.rubric.service.AdminRubricService;
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

@WebMvcTest(AdminRubricController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AdminRubricControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminRubricService adminRubricService;

    private RubricResponse rubricResponse;

    @BeforeEach
    void setUp() {
        RubricCriterionResponse contribution = new RubricCriterionResponse();
        contribution.setId(11L);
        contribution.setName("Contribution");
        contribution.setDescription("Participates consistently");
        contribution.setMaxScore(5);

        RubricCriterionResponse communication = new RubricCriterionResponse();
        communication.setId(12L);
        communication.setName("Communication");
        communication.setDescription("Shares updates clearly");
        communication.setMaxScore(10);

        rubricResponse = new RubricResponse();
        rubricResponse.setId(1L);
        rubricResponse.setName("Teamwork Rubric");
        rubricResponse.setCriteria(List.of(contribution, communication));
    }

    @Test
    @DisplayName("POST /api/admin/rubrics returns 201 when rubric is created")
    void createRubricShouldReturnCreated() throws Exception {
        when(adminRubricService.createRubric(any())).thenReturn(rubricResponse);

        String requestBody = """
                {
                  "name": "Teamwork Rubric",
                  "criteria": [
                    {
                      "name": "Contribution",
                      "description": "Participates consistently",
                      "maxScore": 5
                    },
                    {
                      "name": "Communication",
                      "description": "Shares updates clearly",
                      "maxScore": 10
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/admin/rubrics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Teamwork Rubric"))
                .andExpect(jsonPath("$.criteria[0].name").value("Contribution"))
                .andExpect(jsonPath("$.criteria[1].maxScore").value(10));
    }

    @Test
    @DisplayName("POST /api/admin/rubrics returns 400 for invalid payload")
    void createRubricShouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidBody = """
                {
                  "name": "",
                  "criteria": [
                    {
                      "name": "",
                      "description": "x",
                      "maxScore": 0
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/admin/rubrics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$['fieldErrors']['criteria[0].name']").exists())
                .andExpect(jsonPath("$['fieldErrors']['criteria[0].maxScore']").exists());
    }
}
