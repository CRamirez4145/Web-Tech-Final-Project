package edu.tcu.cs.projectpulse.instructor.report.service;

import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.instructor.report.dto.SectionPeerEvaluationReportResponse;
import edu.tcu.cs.projectpulse.instructor.report.dto.SectionPeerEvaluationStudentResponse;
import edu.tcu.cs.projectpulse.instructor.report.dto.StudentPeerEvaluationReportResponse;
import edu.tcu.cs.projectpulse.instructor.report.dto.StudentWarReportResponse;
import edu.tcu.cs.projectpulse.instructor.report.dto.TeamWarReportResponse;
import edu.tcu.cs.projectpulse.shared.entity.ActiveWeek;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import edu.tcu.cs.projectpulse.shared.repository.ActiveWeekRepository;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InstructorReportService {

    private final SectionRepository sectionRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ActiveWeekRepository activeWeekRepository;
    private final PeerEvaluationRepository peerEvaluationRepository;
    private final WeeklyActivityReportRepository weeklyActivityReportRepository;

    public InstructorReportService(SectionRepository sectionRepository,
                                   TeamRepository teamRepository,
                                   UserRepository userRepository,
                                   ActiveWeekRepository activeWeekRepository,
                                   PeerEvaluationRepository peerEvaluationRepository,
                                   WeeklyActivityReportRepository weeklyActivityReportRepository) {
        this.sectionRepository = sectionRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.activeWeekRepository = activeWeekRepository;
        this.peerEvaluationRepository = peerEvaluationRepository;
        this.weeklyActivityReportRepository = weeklyActivityReportRepository;
    }

    @Transactional(readOnly = true)
    public SectionPeerEvaluationReportResponse getSectionPeerEvaluationReport(Long sectionId, Long activeWeekId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        ActiveWeek activeWeek = getOptionalActiveWeek(activeWeekId);

        List<User> students = userRepository.searchUsers(UserRole.STUDENT, true, sectionId, null, null);
        List<PeerEvaluation> evaluations = peerEvaluationRepository.findAllByFilters(sectionId, null, null, null, activeWeekId);

        Map<Long, ScoreAccumulator> scoresByStudent = new HashMap<>();
        for (PeerEvaluation evaluation : evaluations) {
            scoresByStudent.computeIfAbsent(evaluation.getEvaluatee().getId(), ignored -> new ScoreAccumulator())
                    .addEvaluation(evaluation);
        }

        List<SectionPeerEvaluationStudentResponse> studentResponses = students.stream()
                .map(student -> toSectionStudentResponse(student, scoresByStudent.get(student.getId())))
                .toList();

        SectionPeerEvaluationReportResponse response = new SectionPeerEvaluationReportResponse();
        response.setSectionId(section.getId());
        response.setSectionName(section.getName());
        if (activeWeek != null) {
            response.setActiveWeekId(activeWeek.getId());
            response.setWeekNumber(activeWeek.getWeekNumber());
        }
        response.setStudents(studentResponses);
        return response;
    }

    @Transactional(readOnly = true)
    public TeamWarReportResponse getTeamWarReport(Long teamId, Long activeWeekId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        ActiveWeek activeWeek = getOptionalActiveWeek(activeWeekId);
        List<WarResponse> wars = weeklyActivityReportRepository.findAllByFilters(null, teamId, null, activeWeekId)
                .stream()
                .map(this::toWarResponse)
                .toList();

        TeamWarReportResponse response = new TeamWarReportResponse();
        response.setTeamId(team.getId());
        response.setTeamName(team.getName());
        response.setSectionId(team.getSection().getId());
        response.setSectionName(team.getSection().getName());
        if (activeWeek != null) {
            response.setActiveWeekId(activeWeek.getId());
            response.setWeekNumber(activeWeek.getWeekNumber());
        }
        response.setTotalSubmissions(wars.size());
        response.setWars(wars);
        return response;
    }

    @Transactional(readOnly = true)
    public StudentPeerEvaluationReportResponse getStudentPeerEvaluationReport(Long studentId, Long activeWeekId) {
        User student = getStudent(studentId);
        ActiveWeek activeWeek = getOptionalActiveWeek(activeWeekId);
        List<PeerEvaluation> evaluations = peerEvaluationRepository.findAllByFilters(null, null, studentId, null, activeWeekId);
        ScoreAccumulator accumulator = new ScoreAccumulator();
        evaluations.forEach(accumulator::addEvaluation);

        StudentPeerEvaluationReportResponse response = new StudentPeerEvaluationReportResponse();
        response.setStudentId(student.getId());
        response.setStudentName(fullName(student));
        response.setTeamId(student.getTeam().getId());
        response.setTeamName(student.getTeam().getName());
        response.setSectionId(student.getSection().getId());
        response.setSectionName(student.getSection().getName());
        if (activeWeek != null) {
            response.setActiveWeekId(activeWeek.getId());
            response.setWeekNumber(activeWeek.getWeekNumber());
        }
        response.setOverallAverageScore(accumulator.averageScore());
        response.setEvaluations(evaluations.stream().map(this::toPeerEvaluationResponse).toList());
        return response;
    }

    @Transactional(readOnly = true)
    public StudentWarReportResponse getStudentWarReport(Long studentId, Long activeWeekId) {
        User student = getStudent(studentId);
        ActiveWeek activeWeek = getOptionalActiveWeek(activeWeekId);
        List<WarResponse> wars = weeklyActivityReportRepository.findAllByFilters(null, null, studentId, activeWeekId)
                .stream()
                .map(this::toWarResponse)
                .toList();

        StudentWarReportResponse response = new StudentWarReportResponse();
        response.setStudentId(student.getId());
        response.setStudentName(fullName(student));
        response.setTeamId(student.getTeam().getId());
        response.setTeamName(student.getTeam().getName());
        response.setSectionId(student.getSection().getId());
        response.setSectionName(student.getSection().getName());
        if (activeWeek != null) {
            response.setActiveWeekId(activeWeek.getId());
            response.setWeekNumber(activeWeek.getWeekNumber());
        }
        response.setTotalSubmissions(wars.size());
        response.setWars(wars);
        return response;
    }

    private User getStudent(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        if (user.getRole() != UserRole.STUDENT) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        return user;
    }

    private ActiveWeek getOptionalActiveWeek(Long activeWeekId) {
        if (activeWeekId == null) {
            return null;
        }
        return activeWeekRepository.findById(activeWeekId)
                .orElseThrow(() -> new ResourceNotFoundException("Active week not found with id: " + activeWeekId));
    }

    private SectionPeerEvaluationStudentResponse toSectionStudentResponse(User student, ScoreAccumulator accumulator) {
        SectionPeerEvaluationStudentResponse response = new SectionPeerEvaluationStudentResponse();
        response.setStudentId(student.getId());
        response.setStudentName(fullName(student));
        response.setTeamId(student.getTeam().getId());
        response.setTeamName(student.getTeam().getName());
        response.setAverageScore(accumulator == null ? BigDecimal.ZERO.setScale(2) : accumulator.averageScore());
        response.setEvaluationCount(accumulator == null ? 0 : accumulator.getEvaluationCount());
        return response;
    }

    private WarResponse toWarResponse(WeeklyActivityReport report) {
        WarResponse response = new WarResponse();
        response.setId(report.getId());
        response.setStudentId(report.getStudent().getId());
        response.setStudentName(fullName(report.getStudent()));
        response.setTeamId(report.getTeam().getId());
        response.setSectionId(report.getSection().getId());
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
        response.setEvaluatorName(fullName(evaluation.getEvaluator()));
        response.setEvaluateeId(evaluation.getEvaluatee().getId());
        response.setEvaluateeName(fullName(evaluation.getEvaluatee()));
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

    private String fullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    private static final class ScoreAccumulator {

        private int totalScore;
        private int scoreCount;
        private long evaluationCount;

        private void addEvaluation(PeerEvaluation evaluation) {
            evaluationCount++;
            for (PeerEvaluationCriterionScore criterionScore : evaluation.getCriterionScores()) {
                totalScore += criterionScore.getScore();
                scoreCount++;
            }
        }

        private BigDecimal averageScore() {
            if (scoreCount == 0) {
                return BigDecimal.ZERO.setScale(2);
            }
            return BigDecimal.valueOf(totalScore)
                    .divide(BigDecimal.valueOf(scoreCount), 2, RoundingMode.HALF_UP);
        }

        private long getEvaluationCount() {
            return evaluationCount;
        }
    }
}
