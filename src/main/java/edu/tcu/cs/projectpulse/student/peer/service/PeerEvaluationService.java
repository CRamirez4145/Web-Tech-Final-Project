package edu.tcu.cs.projectpulse.student.peer.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.shared.entity.ActiveWeek;
import edu.tcu.cs.projectpulse.shared.entity.Rubric;
import edu.tcu.cs.projectpulse.shared.entity.RubricCriterion;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.repository.ActiveWeekRepository;
import edu.tcu.cs.projectpulse.shared.repository.RubricCriterionRepository;
import edu.tcu.cs.projectpulse.shared.repository.RubricRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.student.peer.dto.CreatePeerEvaluationRequest;
import edu.tcu.cs.projectpulse.student.peer.dto.PeerCriterionScoreRequest;
import edu.tcu.cs.projectpulse.student.peer.dto.PeerCriterionScoreResponse;
import edu.tcu.cs.projectpulse.student.peer.dto.PeerEvaluationResponse;
import edu.tcu.cs.projectpulse.student.peer.dto.ReceivedPeerEvaluationSummaryResponse;
import edu.tcu.cs.projectpulse.student.peer.entity.PeerEvaluation;
import edu.tcu.cs.projectpulse.student.peer.entity.PeerEvaluationCriterionScore;
import edu.tcu.cs.projectpulse.student.peer.repository.PeerEvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PeerEvaluationService {

    private final PeerEvaluationRepository peerEvaluationRepository;
    private final UserRepository userRepository;
    private final ActiveWeekRepository activeWeekRepository;
    private final RubricRepository rubricRepository;
    private final RubricCriterionRepository rubricCriterionRepository;

    public PeerEvaluationService(PeerEvaluationRepository peerEvaluationRepository,
                                 UserRepository userRepository,
                                 ActiveWeekRepository activeWeekRepository,
                                 RubricRepository rubricRepository,
                                 RubricCriterionRepository rubricCriterionRepository) {
        this.peerEvaluationRepository = peerEvaluationRepository;
        this.userRepository = userRepository;
        this.activeWeekRepository = activeWeekRepository;
        this.rubricRepository = rubricRepository;
        this.rubricCriterionRepository = rubricCriterionRepository;
    }

    @Transactional
    public PeerEvaluationResponse createPeerEvaluation(Long evaluatorId, CreatePeerEvaluationRequest request) {
        User evaluator = userRepository.findById(evaluatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + evaluatorId));

        User evaluatee = userRepository.findById(request.getEvaluateeId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getEvaluateeId()));

        ActiveWeek activeWeek = activeWeekRepository.findById(request.getActiveWeekId())
                .orElseThrow(() -> new ResourceNotFoundException("Active week not found with id: " + request.getActiveWeekId()));

        Rubric rubric = rubricRepository.findById(request.getRubricId())
                .orElseThrow(() -> new ResourceNotFoundException("Rubric not found with id: " + request.getRubricId()));

        validatePeerEvaluationRequest(evaluator, evaluatee, activeWeek, rubric, request);

        Map<Long, RubricCriterion> rubricCriteriaById = loadRubricCriteria(rubric.getId());

        PeerEvaluation evaluation = new PeerEvaluation();
        evaluation.setEvaluator(evaluator);
        evaluation.setEvaluatee(evaluatee);
        evaluation.setTeam(evaluator.getTeam());
        evaluation.setSection(evaluator.getSection());
        evaluation.setActiveWeek(activeWeek);
        evaluation.setRubric(rubric);
        evaluation.setSubmittedAt(LocalDateTime.now());

        for (PeerCriterionScoreRequest scoreRequest : request.getCriterionScores()) {
            RubricCriterion criterion = rubricCriteriaById.get(scoreRequest.getCriterionId());
            if (criterion == null) {
                throw new BusinessRuleException("Criterion " + scoreRequest.getCriterionId() + " does not belong to rubric " + rubric.getId() + ".");
            }

            PeerEvaluationCriterionScore criterionScore = new PeerEvaluationCriterionScore();
            criterionScore.setCriterion(criterion);
            criterionScore.setScore(scoreRequest.getScore());
            criterionScore.setPublicComment(normalize(scoreRequest.getPublicComment()));
            criterionScore.setPrivateComment(normalize(scoreRequest.getPrivateComment()));
            evaluation.addCriterionScore(criterionScore);
        }

        return toResponse(peerEvaluationRepository.save(evaluation));
    }

    @Transactional(readOnly = true)
    public List<PeerEvaluationResponse> getSubmittedEvaluations(Long evaluatorId) {
        validateStudentExists(evaluatorId);
        return peerEvaluationRepository.findByEvaluatorIdOrderBySubmittedAtDesc(evaluatorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReceivedPeerEvaluationSummaryResponse> getReceivedEvaluations(Long evaluateeId) {
        validateStudentExists(evaluateeId);
        return peerEvaluationRepository.findByEvaluateeIdOrderBySubmittedAtDesc(evaluateeId)
                .stream()
                .map(this::toReceivedSummary)
                .toList();
    }

    private void validatePeerEvaluationRequest(User evaluator,
                                               User evaluatee,
                                               ActiveWeek activeWeek,
                                               Rubric rubric,
                                               CreatePeerEvaluationRequest request) {
        if (evaluator.getId().equals(evaluatee.getId())) {
            throw new BusinessRuleException("Students cannot evaluate themselves.");
        }

        if (!evaluator.getTeam().getId().equals(evaluatee.getTeam().getId())) {
            throw new BusinessRuleException("Students can only evaluate teammates.");
        }

        if (!evaluator.getSection().getId().equals(evaluatee.getSection().getId())) {
            throw new BusinessRuleException("Evaluator and evaluatee must belong to the same section.");
        }

        if (peerEvaluationRepository.existsByEvaluatorIdAndEvaluateeIdAndActiveWeekId(
                evaluator.getId(), evaluatee.getId(), activeWeek.getId())) {
            throw new BusinessRuleException("A peer evaluation has already been submitted for this teammate and week.");
        }

        if (request.getCriterionScores().isEmpty()) {
            throw new BusinessRuleException("At least one criterion score is required.");
        }

        ActiveWeek currentActiveWeek = activeWeekRepository.findByActiveTrue()
                .orElseThrow(() -> new BusinessRuleException("No current active week is configured."));

        if (request.getActiveWeekId().equals(currentActiveWeek.getId())) {
            throw new BusinessRuleException("Peer evaluations must be submitted for the previous week, not the current active week.");
        }

        if (activeWeek.getWeekNumber() != currentActiveWeek.getWeekNumber() - 1) {
            throw new BusinessRuleException("Peer evaluations must target the previous week.");
        }

        if (rubricCriterionRepository.findByRubricId(rubric.getId()).isEmpty()) {
            throw new BusinessRuleException("The selected rubric has no criteria.");
        }

        validateNoDuplicateCriteria(request);
    }

    private void validateNoDuplicateCriteria(CreatePeerEvaluationRequest request) {
        Set<Long> criterionIds = new HashSet<>();
        for (PeerCriterionScoreRequest criterionScore : request.getCriterionScores()) {
            if (!criterionIds.add(criterionScore.getCriterionId())) {
                throw new BusinessRuleException("Each rubric criterion can only be scored once per peer evaluation.");
            }
        }
    }

    private Map<Long, RubricCriterion> loadRubricCriteria(Long rubricId) {
        List<RubricCriterion> criteria = rubricCriterionRepository.findByRubricId(rubricId);
        Map<Long, RubricCriterion> criteriaById = new HashMap<>();
        for (RubricCriterion criterion : criteria) {
            criteriaById.put(criterion.getId(), criterion);
        }
        return criteriaById;
    }

    private void validateStudentExists(Long studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
    }

    private PeerEvaluationResponse toResponse(PeerEvaluation evaluation) {
        PeerEvaluationResponse response = new PeerEvaluationResponse();
        response.setId(evaluation.getId());
        response.setEvaluatorId(evaluation.getEvaluator().getId());
        response.setEvaluateeId(evaluation.getEvaluatee().getId());
        response.setEvaluateeName(evaluation.getEvaluatee().getFirstName() + " " + evaluation.getEvaluatee().getLastName());
        response.setTeamId(evaluation.getTeam().getId());
        response.setSectionId(evaluation.getSection().getId());
        response.setActiveWeekId(evaluation.getActiveWeek().getId());
        response.setWeekNumber(evaluation.getActiveWeek().getWeekNumber());
        response.setRubricId(evaluation.getRubric().getId());
        response.setSubmittedAt(evaluation.getSubmittedAt());
        response.setCriterionScores(evaluation.getCriterionScores().stream().map(this::toCriterionScoreResponse).toList());
        return response;
    }

    private PeerCriterionScoreResponse toCriterionScoreResponse(PeerEvaluationCriterionScore criterionScore) {
        PeerCriterionScoreResponse response = new PeerCriterionScoreResponse();
        response.setId(criterionScore.getId());
        response.setCriterionId(criterionScore.getCriterion().getId());
        response.setCriterionName(criterionScore.getCriterion().getName());
        response.setScore(criterionScore.getScore());
        response.setPublicComment(criterionScore.getPublicComment());
        response.setPrivateComment(criterionScore.getPrivateComment());
        return response;
    }

    private ReceivedPeerEvaluationSummaryResponse toReceivedSummary(PeerEvaluation evaluation) {
        ReceivedPeerEvaluationSummaryResponse response = new ReceivedPeerEvaluationSummaryResponse();
        response.setEvaluationId(evaluation.getId());
        response.setEvaluatorId(evaluation.getEvaluator().getId());
        response.setEvaluatorName(evaluation.getEvaluator().getFirstName() + " " + evaluation.getEvaluator().getLastName());
        response.setActiveWeekId(evaluation.getActiveWeek().getId());
        response.setWeekNumber(evaluation.getActiveWeek().getWeekNumber());
        response.setAverageScore(calculateAverageScore(evaluation));
        response.setSubmittedAt(evaluation.getSubmittedAt());
        return response;
    }

    private BigDecimal calculateAverageScore(PeerEvaluation evaluation) {
        if (evaluation.getCriterionScores().isEmpty()) {
            return BigDecimal.ZERO;
        }

        int totalScore = evaluation.getCriterionScores().stream()
                .mapToInt(PeerEvaluationCriterionScore::getScore)
                .sum();

        return BigDecimal.valueOf(totalScore)
                .divide(BigDecimal.valueOf(evaluation.getCriterionScores().size()), 2, RoundingMode.HALF_UP);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
