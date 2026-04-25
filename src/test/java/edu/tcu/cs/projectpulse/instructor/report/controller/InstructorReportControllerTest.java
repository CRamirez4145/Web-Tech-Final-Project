package edu.tcu.cs.projectpulse.instructor.report.controller;

import edu.tcu.cs.projectpulse.common.exception.GlobalExceptionHandler;
import edu.tcu.cs.projectpulse.config.SecurityConfig;
import edu.tcu.cs.projectpulse.instructor.report.dto.SectionPeerEvaluationReportResponse;
import edu.tcu.cs.projectpulse.instructor.report.dto.SectionPeerEvaluationStudentResponse;
import edu.tcu.cs.projectpulse.instructor.report.dto.StudentWarReportResponse;
import edu.tcu.cs.projectpulse.instructor.report.service.InstructorReportService;
import edu.tcu.cs.projectpulse.student.war.dto.WarResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InstructorReportController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class InstructorReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InstructorReportService instructorReportService;

    private SectionPeerEvaluationReportResponse sectionReportResponse;
    private StudentWarReportResponse studentWarReportResponse;

    @BeforeEach
    void setUp() {
        SectionPeerEvaluationStudentResponse studentResponse = new SectionPeerEvaluationStudentResponse();
        studentResponse.setStudentId(2L);
        studentResponse.setStudentName("Alex Carter");
        studentResponse.setTeamId(4L);
        studentResponse.setTeamName("Team Alpha");
        studentResponse.setAverageScore(new BigDecimal("4.00"));
        studentResponse.setEvaluationCount(1);

        sectionReportResponse = new SectionPeerEvaluationReportResponse();
        sectionReportResponse.setSectionId(3L);
        sectionReportResponse.setSectionName("Section 1");
        sectionReportResponse.setActiveWeekId(6L);
        sectionReportResponse.setWeekNumber(6);
        sectionReportResponse.setStudents(List.of(studentResponse));

        WarResponse warResponse = new WarResponse();
        warResponse.setId(21L);
        warResponse.setStudentId(2L);
        warResponse.setStudentName("Alex Carter");
        warResponse.setTeamId(4L);
        warResponse.setSectionId(3L);
        warResponse.setActiveWeekId(6L);
        warResponse.setWeekNumber(6);
        warResponse.setStatus("SUBMITTED");

        studentWarReportResponse = new StudentWarReportResponse();
        studentWarReportResponse.setStudentId(2L);
        studentWarReportResponse.setStudentName("Alex Carter");
        studentWarReportResponse.setTeamId(4L);
        studentWarReportResponse.setTeamName("Team Alpha");
        studentWarReportResponse.setSectionId(3L);
        studentWarReportResponse.setSectionName("Section 1");
        studentWarReportResponse.setActiveWeekId(6L);
        studentWarReportResponse.setWeekNumber(6);
        studentWarReportResponse.setTotalSubmissions(1);
        studentWarReportResponse.setWars(List.of(warResponse));
    }

    @Test
    @DisplayName("GET /api/instructor/reports/sections/{id}/peer-evaluations returns section report")
    void getSectionPeerEvaluationReportShouldReturnReport() throws Exception {
        when(instructorReportService.getSectionPeerEvaluationReport(3L, 6L)).thenReturn(sectionReportResponse);

        mockMvc.perform(get("/api/instructor/reports/sections/3/peer-evaluations")
                        .param("activeWeekId", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students[0].averageScore").value(4.00));
    }

    @Test
    @DisplayName("GET /api/instructor/reports/students/{id}/wars returns student WAR report")
    void getStudentWarReportShouldReturnReport() throws Exception {
        when(instructorReportService.getStudentWarReport(2L, 6L)).thenReturn(studentWarReportResponse);

        mockMvc.perform(get("/api/instructor/reports/students/2/wars")
                        .param("activeWeekId", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wars[0].studentName").value("Alex Carter"))
                .andExpect(jsonPath("$.totalSubmissions").value(1));
    }
}
