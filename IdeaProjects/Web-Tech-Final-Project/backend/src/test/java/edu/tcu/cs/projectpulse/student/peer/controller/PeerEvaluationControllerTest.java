package edu.tcu.cs.projectpulse.student.peer.controller;

import edu.tcu.cs.projectpulse.common.exception.GlobalExceptionHandler;
import edu.tcu.cs.projectpulse.config.SecurityConfig;
import edu.tcu.cs.projectpulse.student.peer.dto.PeerCriterionScoreResponse;
import edu.tcu.cs.projectpulse.student.peer.dto.PeerEvaluationResponse;
import edu.tcu.cs.projectpulse.student.peer.dto.ReceivedPeerEvaluationSummaryResponse;
import edu.tcu.cs.projectpulse.student.peer.service.PeerEvaluationService;
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

@WebMvcTest(PeerEvaluationController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class PeerEvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PeerEvaluationService peerEvaluationService;

    private PeerEvaluationResponse peerEvaluationResponse;
    private ReceivedPeerEvaluationSummaryResponse receivedSummaryResponse;

    @BeforeEach
    void setUp() {
        PeerCriterionScoreResponse scoreResponse = new PeerCriterionScoreResponse();
        scoreResponse.setId(11L);
        scoreResponse.setCriterionId(21L);
        scoreResponse.setCriterionName("Contribution");
        scoreResponse.setScore(4);
        scoreResponse.setPublicComment("Consistent work");
        scoreResponse.setPrivateComment("No concerns");

        peerEvaluationResponse = new PeerEvaluationResponse();
        peerEvaluationResponse.setId(5L);
        peerEvaluationResponse.setEvaluatorId(1L);
        peerEvaluationResponse.setEvaluateeId(2L);
        peerEvaluationResponse.setEvaluateeName("Alex Carter");
        peerEvaluationResponse.setTeamId(3L);
        peerEvaluationResponse.setSectionId(4L);
        peerEvaluationResponse.setActiveWeekId(6L);
        peerEvaluationResponse.setWeekNumber(6);
        peerEvaluationResponse.setRubricId(7L);
        peerEvaluationResponse.setSubmittedAt(LocalDateTime.of(2026, 4, 20, 11, 15));
        peerEvaluationResponse.setCriterionScores(List.of(scoreResponse));

        receivedSummaryResponse = new ReceivedPeerEvaluationSummaryResponse();
        receivedSummaryResponse.setEvaluationId(5L);
        receivedSummaryResponse.setEvaluatorId(1L);
        receivedSummaryResponse.setEvaluatorName("Taylor Reed");
        receivedSummaryResponse.setActiveWeekId(6L);
        receivedSummaryResponse.setWeekNumber(6);
        receivedSummaryResponse.setAverageScore(new BigDecimal("4.00"));
        receivedSummaryResponse.setSubmittedAt(LocalDateTime.of(2026, 4, 20, 11, 15));
    }

    @Test
    @DisplayName("POST /api/student/peer-evaluations returns 201 when evaluation is submitted")
    void createPeerEvaluationShouldReturnCreated() throws Exception {
        when(peerEvaluationService.createPeerEvaluation(eq(1L), any())).thenReturn(peerEvaluationResponse);

        String requestBody = """
                {
                  "evaluateeId": 2,
                  "activeWeekId": 6,
                  "rubricId": 7,
                  "criterionScores": [
                    {
                      "criterionId": 21,
                      "score": 4,
                      "publicComment": "Consistent work",
                      "privateComment": "No concerns"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/student/peer-evaluations")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.evaluateeId").value(2))
                .andExpect(jsonPath("$.criterionScores[0].score").value(4));
    }

    @Test
    @DisplayName("POST /api/student/peer-evaluations returns 400 for invalid score")
    void createPeerEvaluationShouldReturnBadRequestForInvalidScore() throws Exception {
        String invalidBody = """
                {
                  "evaluateeId": 2,
                  "activeWeekId": 6,
                  "rubricId": 7,
                  "criterionScores": [
                    {
                      "criterionId": 21,
                      "score": -1
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/student/peer-evaluations")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."));
    }

    @Test
    @DisplayName("GET /api/student/peer-evaluations/submitted returns submitted evaluations")
    void getSubmittedEvaluationsShouldReturnList() throws Exception {
        when(peerEvaluationService.getSubmittedEvaluations(1L)).thenReturn(List.of(peerEvaluationResponse));

        mockMvc.perform(get("/api/student/peer-evaluations/submitted")
                        .header("X-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].evaluateeName").value("Alex Carter"));
    }

    @Test
    @DisplayName("GET /api/student/peer-evaluations/received returns received summaries")
    void getReceivedEvaluationsShouldReturnList() throws Exception {
        when(peerEvaluationService.getReceivedEvaluations(1L)).thenReturn(List.of(receivedSummaryResponse));

        mockMvc.perform(get("/api/student/peer-evaluations/received")
                        .header("X-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].evaluationId").value(5))
                .andExpect(jsonPath("$[0].averageScore").value(4.00));
    }
}
