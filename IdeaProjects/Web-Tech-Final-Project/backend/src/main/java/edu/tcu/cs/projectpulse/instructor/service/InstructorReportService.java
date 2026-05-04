package edu.tcu.cs.projectpulse.instructor.service;

import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.shared.repository.SectionRepository;
import edu.tcu.cs.projectpulse.shared.repository.TeamRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.student.peer.dto.PeerCriterionScoreResponse;
import edu.tcu.cs.projectpulse.student.peer.dto.PeerEvaluationResponse;
import edu.tcu.cs.projectpulse.student.peer.entity.PeerEvaluation;
import edu.tcu.cs.projectpulse.student.peer.entity.PeerEvaluationCriterionScore;
import edu.tcu.cs.projectpulse.student.peer.repository.PeerEvaluationRepository;
import edu.tcu.cs.projectpulse.student.war.dto.WarActivityResponse;
import edu.tcu.cs.projectpulse.student.war.dto.WarResponse;
import edu.tcu.cs.projectpulse.student.war.entity.WarActivity;
import edu.tcu.cs.projectpulse.student.war.entity.WeeklyActivityReport;
import edu.tcu.cs.projectpulse.student.war.repository.WeeklyActivityReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InstructorReportService {

    private final WeeklyActivityReportRepository weeklyActivityReportRepository;
    private final PeerEvaluationRepository peerEvaluationRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;

    public InstructorReportService(WeeklyActivityReportRepository weeklyActivityReportRepository,
                                   PeerEvaluationRepository peerEvaluationRepository,
                                   TeamRepository teamRepository,
                                   UserRepository userRepository,
                                   SectionRepository sectionRepository) {
        this.weeklyActivityReportRepository = weeklyActivityReportRepository;
        this.peerEvaluationRepository = peerEvaluationRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional(readOnly = true)
    public List<WarResponse> getTeamWars(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team not found with id: " + teamId);
        }
        return weeklyActivityReportRepository.findByTeamIdOrderBySubmittedAtDesc(teamId).stream()
                .map(this::toWarResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WarResponse> getStudentWars(Long studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        return weeklyActivityReportRepository.findByStudentIdOrderBySubmittedAtDesc(studentId).stream()
                .map(this::toWarResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PeerEvaluationResponse> getSectionPeerEvaluations(Long sectionId) {
        if (!sectionRepository.existsById(sectionId)) {
            throw new ResourceNotFoundException("Section not found with id: " + sectionId);
        }
        return peerEvaluationRepository.findBySectionIdOrderBySubmittedAtDesc(sectionId).stream()
                .map(this::toPeerEvaluationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PeerEvaluationResponse> getStudentPeerEvaluations(Long studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        return peerEvaluationRepository.findByEvaluateeIdOrderBySubmittedAtDesc(studentId).stream()
                .map(this::toPeerEvaluationResponse)
                .toList();
    }

    private WarResponse toWarResponse(WeeklyActivityReport report) {
        WarResponse response = new WarResponse();
        response.setId(report.getId());
        response.setStudentId(report.getStudent().getId());
        response.setStudentName(report.getStudent().getFirstName() + " " + report.getStudent().getLastName());
        response.setTeamId(report.getTeam().getId());
        response.setTeamName(report.getTeam().getName());
        response.setSectionId(report.getSection().getId());
        response.setSectionName(report.getSection().getName());
        response.setActiveWeekId(report.getActiveWeek().getId());
        response.setWeekNumber(report.getActiveWeek().getWeekNumber());
        response.setSubmittedAt(report.getSubmittedAt());
        response.setStatus(report.getStatus().name());
        response.setActivities(report.getActivities().stream().map(this::toWarActivityResponse).toList());
        return response;
    }

    private WarActivityResponse toWarActivityResponse(WarActivity activity) {
        WarActivityResponse response = new WarActivityResponse();
        response.setId(activity.getId());
        response.setDescription(activity.getDescription());
        response.setHoursSpent(activity.getHoursSpent());
        response.setCategory(activity.getCategory());
        return response;
    }

    private PeerEvaluationResponse toPeerEvaluationResponse(PeerEvaluation evaluation) {
        PeerEvaluationResponse response = new PeerEvaluationResponse();
        response.setId(evaluation.getId());
        response.setEvaluatorId(evaluation.getEvaluator().getId());
        response.setEvaluatorName(evaluation.getEvaluator().getFirstName() + " " + evaluation.getEvaluator().getLastName());
        response.setEvaluateeId(evaluation.getEvaluatee().getId());
        response.setEvaluateeName(evaluation.getEvaluatee().getFirstName() + " " + evaluation.getEvaluatee().getLastName());
        response.setTeamId(evaluation.getTeam().getId());
        response.setTeamName(evaluation.getTeam().getName());
        response.setSectionId(evaluation.getSection().getId());
        response.setSectionName(evaluation.getSection().getName());
        response.setActiveWeekId(evaluation.getActiveWeek().getId());
        response.setWeekNumber(evaluation.getActiveWeek().getWeekNumber());
        response.setRubricId(evaluation.getRubric().getId());
        response.setRubricName(evaluation.getRubric().getName());
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
}
