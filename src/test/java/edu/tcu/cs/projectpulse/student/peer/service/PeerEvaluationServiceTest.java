package edu.tcu.cs.projectpulse.student.peer.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.shared.entity.ActiveWeek;
import edu.tcu.cs.projectpulse.shared.entity.Rubric;
import edu.tcu.cs.projectpulse.shared.entity.RubricCriterion;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.repository.ActiveWeekRepository;
import edu.tcu.cs.projectpulse.shared.repository.RubricCriterionRepository;
import edu.tcu.cs.projectpulse.shared.repository.RubricRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.student.peer.dto.CreatePeerEvaluationRequest;
import edu.tcu.cs.projectpulse.student.peer.dto.PeerCriterionScoreRequest;
import edu.tcu.cs.projectpulse.student.peer.dto.PeerEvaluationResponse;
import edu.tcu.cs.projectpulse.student.peer.dto.ReceivedPeerEvaluationSummaryResponse;
import edu.tcu.cs.projectpulse.student.peer.entity.PeerEvaluation;
import edu.tcu.cs.projectpulse.student.peer.entity.PeerEvaluationCriterionScore;
import edu.tcu.cs.projectpulse.student.peer.repository.PeerEvaluationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeerEvaluationServiceTest {

    @Mock
    private PeerEvaluationRepository peerEvaluationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActiveWeekRepository activeWeekRepository;

    @Mock
    private RubricRepository rubricRepository;

    @Mock
    private RubricCriterionRepository rubricCriterionRepository;

    @InjectMocks
    private PeerEvaluationService peerEvaluationService;

    private User evaluator;
    private User evaluatee;
    private ActiveWeek previousWeek;
    private ActiveWeek currentWeek;
    private Rubric rubric;
    private RubricCriterion criterion;
    private CreatePeerEvaluationRequest request;

    @BeforeEach
    void setUp() {
        Section section = new Section();
        section.setId(4L);
        section.setName("Section 1");

        Team team = new Team();
        team.setId(3L);
        team.setName("Team Alpha");
        team.setSection(section);

        evaluator = new User();
        evaluator.setId(1L);
        evaluator.setFirstName("Javier");
        evaluator.setLastName("Lopez");
        evaluator.setEmail("javier@tcu.edu");
        evaluator.setTeam(team);
        evaluator.setSection(section);

        evaluatee = new User();
        evaluatee.setId(2L);
        evaluatee.setFirstName("Alex");
        evaluatee.setLastName("Carter");
        evaluatee.setEmail("alex@tcu.edu");
        evaluatee.setTeam(team);
        evaluatee.setSection(section);

        previousWeek = new ActiveWeek();
        previousWeek.setId(6L);
        previousWeek.setWeekNumber(6);
        previousWeek.setStartDate(LocalDate.of(2026, 4, 6));
        previousWeek.setEndDate(LocalDate.of(2026, 4, 12));
        previousWeek.setActive(false);

        currentWeek = new ActiveWeek();
        currentWeek.setId(7L);
        currentWeek.setWeekNumber(7);
        currentWeek.setStartDate(LocalDate.of(2026, 4, 13));
        currentWeek.setEndDate(LocalDate.of(2026, 4, 19));
        currentWeek.setActive(true);

        rubric = new Rubric();
        rubric.setId(8L);
        rubric.setName("Peer Rubric");

        criterion = new RubricCriterion();
        criterion.setId(9L);
        criterion.setName("Contribution");
        criterion.setRubric(rubric);

        PeerCriterionScoreRequest scoreRequest = new PeerCriterionScoreRequest();
        scoreRequest.setCriterionId(9L);
        scoreRequest.setScore(4);
        scoreRequest.setPublicComment("Solid contribution");
        scoreRequest.setPrivateComment("No issues");

        request = new CreatePeerEvaluationRequest();
        request.setEvaluateeId(2L);
        request.setActiveWeekId(6L);
        request.setRubricId(8L);
        request.setCriterionScores(List.of(scoreRequest));
    }

    @Test
    @DisplayName("createPeerEvaluation saves a valid peer evaluation")
    void createPeerEvaluationShouldSaveEvaluation() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(evaluator));
        when(userRepository.findById(2L)).thenReturn(Optional.of(evaluatee));
        when(activeWeekRepository.findById(6L)).thenReturn(Optional.of(previousWeek));
        when(activeWeekRepository.findByActiveTrue()).thenReturn(Optional.of(currentWeek));
        when(rubricRepository.findById(8L)).thenReturn(Optional.of(rubric));
        when(rubricCriterionRepository.findByRubricId(8L)).thenReturn(List.of(criterion));
        when(peerEvaluationRepository.existsByEvaluatorIdAndEvaluateeIdAndActiveWeekId(1L, 2L, 6L)).thenReturn(false);
        when(peerEvaluationRepository.save(any(PeerEvaluation.class))).thenAnswer(invocation -> {
            PeerEvaluation evaluation = invocation.getArgument(0);
            evaluation.setId(30L);
            evaluation.getCriterionScores().get(0).setId(40L);
            return evaluation;
        });

        PeerEvaluationResponse response = peerEvaluationService.createPeerEvaluation(1L, request);

        assertNotNull(response);
        assertEquals(30L, response.getId());
        assertEquals(2L, response.getEvaluateeId());
        assertEquals(1, response.getCriterionScores().size());
    }

    @Test
    @DisplayName("createPeerEvaluation rejects self-evaluation")
    void createPeerEvaluationShouldRejectSelfEvaluation() {
        request.setEvaluateeId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(evaluator));
        when(userRepository.findById(1L)).thenReturn(Optional.of(evaluator));
        when(activeWeekRepository.findById(6L)).thenReturn(Optional.of(previousWeek));
        when(rubricRepository.findById(8L)).thenReturn(Optional.of(rubric));

        assertThrows(BusinessRuleException.class, () -> peerEvaluationService.createPeerEvaluation(1L, request));
    }

    @Test
    @DisplayName("createPeerEvaluation rejects duplicate evaluations")
    void createPeerEvaluationShouldRejectDuplicateEvaluation() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(evaluator));
        when(userRepository.findById(2L)).thenReturn(Optional.of(evaluatee));
        when(activeWeekRepository.findById(6L)).thenReturn(Optional.of(previousWeek));
        when(rubricRepository.findById(8L)).thenReturn(Optional.of(rubric));
        when(peerEvaluationRepository.existsByEvaluatorIdAndEvaluateeIdAndActiveWeekId(1L, 2L, 6L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> peerEvaluationService.createPeerEvaluation(1L, request));
    }

    @Test
    @DisplayName("createPeerEvaluation rejects non-previous weeks")
    void createPeerEvaluationShouldRejectWrongWeek() {
        request.setActiveWeekId(7L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(evaluator));
        when(userRepository.findById(2L)).thenReturn(Optional.of(evaluatee));
        when(activeWeekRepository.findById(7L)).thenReturn(Optional.of(currentWeek));
        when(activeWeekRepository.findByActiveTrue()).thenReturn(Optional.of(currentWeek));
        when(rubricRepository.findById(8L)).thenReturn(Optional.of(rubric));
        when(peerEvaluationRepository.existsByEvaluatorIdAndEvaluateeIdAndActiveWeekId(1L, 2L, 7L)).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> peerEvaluationService.createPeerEvaluation(1L, request));
    }

    @Test
    @DisplayName("createPeerEvaluation rejects duplicate criterion ids in one submission")
    void createPeerEvaluationShouldRejectDuplicateCriteria() {
        PeerCriterionScoreRequest secondScore = new PeerCriterionScoreRequest();
        secondScore.setCriterionId(9L);
        secondScore.setScore(3);
        request.setCriterionScores(List.of(request.getCriterionScores().get(0), secondScore));

        when(userRepository.findById(1L)).thenReturn(Optional.of(evaluator));
        when(userRepository.findById(2L)).thenReturn(Optional.of(evaluatee));
        when(activeWeekRepository.findById(6L)).thenReturn(Optional.of(previousWeek));
        when(activeWeekRepository.findByActiveTrue()).thenReturn(Optional.of(currentWeek));
        when(rubricRepository.findById(8L)).thenReturn(Optional.of(rubric));
        when(peerEvaluationRepository.existsByEvaluatorIdAndEvaluateeIdAndActiveWeekId(1L, 2L, 6L)).thenReturn(false);
        when(rubricCriterionRepository.findByRubricId(8L)).thenReturn(List.of(criterion));

        assertThrows(BusinessRuleException.class, () -> peerEvaluationService.createPeerEvaluation(1L, request));
    }

    @Test
    @DisplayName("getSubmittedEvaluations returns submitted evaluations")
    void getSubmittedEvaluationsShouldReturnList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(peerEvaluationRepository.findByEvaluatorIdOrderBySubmittedAtDesc(1L)).thenReturn(List.of(buildEvaluation()));

        List<PeerEvaluationResponse> responses = peerEvaluationService.getSubmittedEvaluations(1L);

        assertEquals(1, responses.size());
        assertEquals(30L, responses.get(0).getId());
    }

    @Test
    @DisplayName("getReceivedEvaluations returns received summaries")
    void getReceivedEvaluationsShouldReturnSummaries() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(peerEvaluationRepository.findByEvaluateeIdOrderBySubmittedAtDesc(2L)).thenReturn(List.of(buildEvaluation()));

        List<ReceivedPeerEvaluationSummaryResponse> responses = peerEvaluationService.getReceivedEvaluations(2L);

        assertEquals(1, responses.size());
        assertEquals(30L, responses.get(0).getEvaluationId());
        assertEquals("4.00", responses.get(0).getAverageScore().toString());
    }

    private PeerEvaluation buildEvaluation() {
        PeerEvaluation evaluation = new PeerEvaluation();
        evaluation.setId(30L);
        evaluation.setEvaluator(evaluator);
        evaluation.setEvaluatee(evaluatee);
        evaluation.setTeam(evaluator.getTeam());
        evaluation.setSection(evaluator.getSection());
        evaluation.setActiveWeek(previousWeek);
        evaluation.setRubric(rubric);
        evaluation.setSubmittedAt(LocalDateTime.of(2026, 4, 20, 12, 0));

        PeerEvaluationCriterionScore criterionScore = new PeerEvaluationCriterionScore();
        criterionScore.setId(40L);
        criterionScore.setCriterion(criterion);
        criterionScore.setScore(4);
        criterionScore.setPublicComment("Solid contribution");
        criterionScore.setPrivateComment("No issues");
        evaluation.addCriterionScore(criterionScore);

        return evaluation;
    }
}
