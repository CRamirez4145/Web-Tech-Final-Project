package edu.tcu.cs.projectpulse.student.war.controller;

import edu.tcu.cs.projectpulse.common.exception.GlobalExceptionHandler;
import edu.tcu.cs.projectpulse.config.SecurityConfig;
import edu.tcu.cs.projectpulse.student.war.dto.WarActivityResponse;
import edu.tcu.cs.projectpulse.student.war.dto.WarResponse;
import edu.tcu.cs.projectpulse.student.war.service.WarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WarController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class WarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WarService warService;

    private WarResponse warResponse;

    @BeforeEach
    void setUp() {
        WarActivityResponse activityResponse = new WarActivityResponse();
        activityResponse.setId(10L);
        activityResponse.setDescription("Implemented login endpoint");
        activityResponse.setHoursSpent(new BigDecimal("3.50"));
        activityResponse.setCategory("Backend");

        warResponse = new WarResponse();
        warResponse.setId(1L);
        warResponse.setStudentId(1L);
        warResponse.setTeamId(2L);
        warResponse.setSectionId(3L);
        warResponse.setActiveWeekId(4L);
        warResponse.setWeekNumber(7);
        warResponse.setSubmittedAt(LocalDateTime.of(2026, 4, 20, 10, 30));
        warResponse.setStatus("SUBMITTED");
        warResponse.setActivities(List.of(activityResponse));
    }

    @Test
    @DisplayName("POST /api/student/wars returns 201 when WAR is created")
    void createWarShouldReturnCreated() throws Exception {
        when(warService.createWar(eq(1L), any())).thenReturn(warResponse);

        String requestBody = """
                {
                  "activeWeekId": 4,
                  "activities": [
                    {
                      "description": "Implemented login endpoint",
                      "hoursSpent": 3.5,
                      "category": "Backend"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/student/wars")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.activities[0].description").value("Implemented login endpoint"));
    }

    @Test
    @DisplayName("GET /api/student/wars returns the student's WAR list")
    void getWarsShouldReturnWarList() throws Exception {
        when(warService.getWarsForStudent(1L)).thenReturn(List.of(warResponse));

        mockMvc.perform(get("/api/student/wars")
                        .header("X-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("SUBMITTED"));
    }

    @Test
    @DisplayName("GET /api/student/wars/{id} returns one WAR")
    void getWarShouldReturnSingleWar() throws Exception {
        when(warService.getWarForStudent(1L, 1L)).thenReturn(warResponse);

        mockMvc.perform(get("/api/student/wars/1")
                        .header("X-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.weekNumber").value(7));
    }

    @Test
    @DisplayName("POST /api/student/wars returns 400 for invalid request data")
    void createWarShouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidBody = """
                {
                  "activities": [
                    {
                      "description": "",
                      "hoursSpent": 0
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/student/wars")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.fieldErrors.activeWeekId").exists())
                .andExpect(jsonPath("$.fieldErrors['activities[0].description']").exists())
                .andExpect(jsonPath("$.fieldErrors['activities[0].hoursSpent']").exists());
    }
}
