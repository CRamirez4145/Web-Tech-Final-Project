package edu.tcu.cs.projectpulse.admin.section.controller;

import edu.tcu.cs.projectpulse.admin.section.dto.CreateSectionRequest;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionDetailResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionStudentResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionSummaryResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionTeamResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionWeekResponse;
import edu.tcu.cs.projectpulse.admin.section.service.AdminSectionService;
import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.GlobalExceptionHandler;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminSectionController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AdminSectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminSectionService adminSectionService;

    private SectionSummaryResponse sectionA;
    private SectionSummaryResponse sectionB;
    private SectionDetailResponse sectionDetail;

    @BeforeEach
    void setUp() {
        sectionA = new SectionSummaryResponse();
        sectionA.setId(1L);
        sectionA.setName("Section A");

        sectionB = new SectionSummaryResponse();
        sectionB.setId(2L);
        sectionB.setName("Section B");

        SectionTeamResponse team = new SectionTeamResponse();
        team.setId(10L);
        team.setName("Team Alpha");

        SectionStudentResponse student = new SectionStudentResponse();
        student.setId(20L);
        student.setFirstName("Ada");
        student.setLastName("Lovelace");
        student.setEmail("ada@tcu.edu");
        student.setTeamId(10L);
        student.setTeamName("Team Alpha");

        SectionWeekResponse week = new SectionWeekResponse();
        week.setId(11L);
        week.setWeekNumber(1);
        week.setStartDate(java.time.LocalDate.of(2026, 1, 1));
        week.setEndDate(java.time.LocalDate.of(2026, 1, 7));

        sectionDetail = new SectionDetailResponse();
        sectionDetail.setId(1L);
        sectionDetail.setName("Senior Design A");
        sectionDetail.setStartDate(java.time.LocalDate.of(2026, 8, 20));
        sectionDetail.setEndDate(java.time.LocalDate.of(2026, 12, 10));
        sectionDetail.setRubricId(7L);
        sectionDetail.setRubricName("Teamwork Rubric");
        sectionDetail.setSelectedWeeks(List.of(week));
        sectionDetail.setTeams(List.of(team));
        sectionDetail.setStudents(List.of(student));
    }

    @Test
    @DisplayName("POST /api/sections returns 201 when section is created")
    void createSectionShouldReturnCreated() throws Exception {
        when(adminSectionService.createSection(any())).thenReturn(sectionDetail);

        String requestBody = """
                {
                  "name": "Senior Design A",
                  "startDate": "2026-08-20",
                  "endDate": "2026-12-10",
                  "rubricId": 7
                }
                """;

        mockMvc.perform(post("/api/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Senior Design A"))
                .andExpect(jsonPath("$.rubricId").value(7))
                .andExpect(jsonPath("$.rubricName").value("Teamwork Rubric"));
    }

    @Test
    @DisplayName("POST /api/sections returns 400 for invalid payload")
    void createSectionShouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidBody = """
                {
                  "name": "",
                  "startDate": null,
                  "endDate": null,
                  "rubricId": null
                }
                """;

        mockMvc.perform(post("/api/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.startDate").exists())
                .andExpect(jsonPath("$.fieldErrors.rubricId").exists());
    }

    @Test
    @DisplayName("POST /api/sections returns 400 when section name is duplicate")
    void createSectionShouldReturnBadRequestForDuplicateName() throws Exception {
        when(adminSectionService.createSection(any())).thenThrow(new BusinessRuleException("Section name is already in use."));

        String requestBody = """
                {
                  "name": "Senior Design A",
                  "startDate": "2026-08-20",
                  "endDate": "2026-12-10",
                  "rubricId": 7
                }
                """;

        mockMvc.perform(post("/api/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Section name is already in use."));
    }

    @Test
    @DisplayName("PUT /api/sections/{id} returns updated section details")
    void updateSectionShouldReturnUpdatedSection() throws Exception {
        when(adminSectionService.updateSection(any(), any())).thenReturn(sectionDetail);

        String requestBody = """
                {
                  "name": "Senior Design A",
                  "startDate": "2026-08-20",
                  "endDate": "2026-12-10",
                  "rubricId": 7
                }
                """;

        mockMvc.perform(put("/api/sections/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Senior Design A"))
                .andExpect(jsonPath("$.rubricId").value(7));
    }

    @Test
    @DisplayName("PUT /api/sections/{id} returns 404 when section is missing")
    void updateSectionShouldReturnNotFoundWhenMissing() throws Exception {
        when(adminSectionService.updateSection(any(), any())).thenThrow(new ResourceNotFoundException("Section not found with id: 99"));

        String requestBody = """
                {
                  "name": "Senior Design A",
                  "startDate": "2026-08-20",
                  "endDate": "2026-12-10",
                  "rubricId": 7
                }
                """;

        mockMvc.perform(put("/api/sections/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Section not found with id: 99"));
    }

    @Test
    @DisplayName("PUT /api/sections/{id} returns 400 when validation fails")
    void updateSectionShouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidBody = """
                {
                  "name": "",
                  "startDate": null,
                  "endDate": null,
                  "rubricId": null
                }
                """;

        mockMvc.perform(put("/api/sections/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.startDate").exists())
                .andExpect(jsonPath("$.fieldErrors.rubricId").exists());
    }

    @Test
    @DisplayName("PUT /api/sections/{id}/weeks returns selected weeks")
    void setSectionWeeksShouldReturnUpdatedWeeks() throws Exception {
        when(adminSectionService.setSectionWeeks(any(), any())).thenReturn(sectionDetail);

        String requestBody = """
                {
                  "weekIds": [11]
                }
                """;

        mockMvc.perform(put("/api/sections/1/weeks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selectedWeeks[0].weekNumber").value(1))
                .andExpect(jsonPath("$.selectedWeeks[0].id").value(11));
    }

    @Test
    @DisplayName("PUT /api/sections/{id}/weeks returns 400 for invalid payload")
    void setSectionWeeksShouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidBody = """
                {
                  "weekIds": null
                }
                """;

        mockMvc.perform(put("/api/sections/1/weeks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.fieldErrors.weekIds").exists());
    }

    @Test
    @DisplayName("PUT /api/sections/{id}/weeks returns 400 when active weeks are selected")
    void setSectionWeeksShouldReturnBadRequestForActiveWeek() throws Exception {
        when(adminSectionService.setSectionWeeks(any(), any())).thenThrow(new BusinessRuleException("Only inactive weeks can be selected for a section."));

        String requestBody = """
                {
                  "weekIds": [11]
                }
                """;

        mockMvc.perform(put("/api/sections/1/weeks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only inactive weeks can be selected for a section."));
    }

    @Test
    @DisplayName("GET /api/sections returns a sorted section list")
    void findSectionsShouldReturnSectionList() throws Exception {
        when(adminSectionService.findSections(null)).thenReturn(List.of(sectionA, sectionB));

        mockMvc.perform(get("/api/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Section A"))
                .andExpect(jsonPath("$[1].name").value("Section B"));
    }

    @Test
    @DisplayName("GET /api/sections passes the name filter through to the service")
    void findSectionsShouldPassNameFilter() throws Exception {
        when(adminSectionService.findSections("senior")).thenReturn(List.of(sectionA));

        mockMvc.perform(get("/api/sections").param("name", "senior"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Section A"));
    }

    @Test
    @DisplayName("GET /api/sections returns empty array when nothing matches")
    void findSectionsShouldReturnEmptyArray() throws Exception {
        when(adminSectionService.findSections("missing")).thenReturn(List.of());

        mockMvc.perform(get("/api/sections").param("name", "missing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/sections/{id} returns section details")
    void getSectionShouldReturnDetails() throws Exception {
        when(adminSectionService.getSection(1L)).thenReturn(sectionDetail);

        mockMvc.perform(get("/api/sections/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Senior Design A"))
                .andExpect(jsonPath("$.teams[0].name").value("Team Alpha"))
                .andExpect(jsonPath("$.students[0].email").value("ada@tcu.edu"));
    }

    @Test
    @DisplayName("GET /api/sections/{id} returns 404 when section is missing")
    void getSectionShouldReturnNotFoundWhenMissing() throws Exception {
        when(adminSectionService.getSection(99L)).thenThrow(new ResourceNotFoundException("Section not found with id: 99"));

        mockMvc.perform(get("/api/sections/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Section not found with id: 99"));
    }
}
