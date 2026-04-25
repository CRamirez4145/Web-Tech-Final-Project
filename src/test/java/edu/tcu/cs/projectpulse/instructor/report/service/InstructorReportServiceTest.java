package edu.tcu.cs.projectpulse.instructor.report.service;

import edu.tcu.cs.projectpulse.instructor.report.dto.SectionPeerEvaluationReportResponse;
import edu.tcu.cs.projectpulse.instructor.report.dto.StudentWarReportResponse;
import edu.tcu.cs.projectpulse.shared.entity.ActiveWeek;
import edu.tcu.cs.projectpulse.shared.entity.Rubric;
import edu.tcu.cs.projectpulse.shared.entity.RubricCriterion;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import edu.tcu.cs.projectpulse.shared.repository.ActiveWeekRepository;
import edu.tcu.cs.projectpulse.shared.repository.SectionRepository;
import edu.tcu.cs.projectpulse.shared.repository.TeamRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.student.peer.entity.PeerEvaluation;
import edu.tcu.cs.projectpulse.student.peer.entity.PeerEvaluationCriterionScore;
import edu.tcu.cs.projectpulse.student.peer.repository.PeerEvaluationRepository;
import edu.tcu.cs.projectpulse.student.war.entity.WarActivity;
import edu.tcu.cs.projectpulse.student.war.entity.WarStatus;
import edu.tcu.cs.projectpulse.student.war.entity.WeeklyActivityReport;
import edu.tcu.cs.projectpulse.student.war.repository.WeeklyActivityReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstructorReportServiceTest {

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActiveWeekRepository activeWeekRepository;

    @Mock
    private PeerEvaluationRepository peerEvaluationRepository;

    @Mock
    private WeeklyActivityReportRepository weeklyActivityReportRepository;

    @InjectMocks
    private InstructorReportService instructorReportService;

    private Section section;
    private Team team;
    private ActiveWeek activeWeek;
    private User evaluator;
    private User evaluatee;

    @BeforeEach
    void setUp() {
        section = new Section();
        section.setId(3L);
        section.setName("Section 1");

        team = new Team();
        team.setId(4L);
        team.setName("Team Alpha");
        team.setSection(section);

        activeWeek = new ActiveWeek();
        activeWeek.setId(6L);
        activeWeek.setWeekNumber(6);
        activeWeek.setStartDate(LocalDate.of(2026, 4, 6));
        activeWeek.setEndDate(LocalDate.of(2026, 4, 12));
        activeWeek.setActive(false);

        evaluator = buildStudent(1L, "Taylor", "Reed");
        evaluatee = buildStudent(2L, "Alex", "Carter");
    }

    @Test
    @DisplayName("getSectionPeerEvaluationReport calculates average score per student")
    void getSectionPeerEvaluationReportShouldAggregateScores() {
        PeerEvaluation evaluation = buildEvaluation();

        when(sectionRepository.findById(3L)).thenReturn(Optional.of(section));
        when(activeWeekRepository.findById(6L)).thenReturn(Optional.of(activeWeek));
        when(userRepository.searchUsers(UserRole.STUDENT, true, 3L, null, null)).thenReturn(List.of(evaluatee));
        when(peerEvaluationRepository.findAllByFilters(3L, null, null, null, 6L)).thenReturn(List.of(evaluation));

        SectionPeerEvaluationReportResponse response = instructorReportService.getSectionPeerEvaluationReport(3L, 6L);

        assertEquals(3L, response.getSectionId());
        assertEquals(1, response.getStudents().size());
        assertEquals("4.00", response.getStudents().get(0).getAverageScore().toString());
        assertEquals(1, response.getStudents().get(0).getEvaluationCount());
    }

    @Test
    @DisplayName("getStudentWarReport returns the student's weekly reports")
    void getStudentWarReportShouldReturnWars() {
        WeeklyActivityReport war = buildWar();

        when(userRepository.findById(2L)).thenReturn(Optional.of(evaluatee));
        when(activeWeekRepository.findById(6L)).thenReturn(Optional.of(activeWeek));
        when(weeklyActivityReportRepository.findAllByFilters(null, null, 2L, 6L)).thenReturn(List.of(war));

        StudentWarReportResponse response = instructorReportService.getStudentWarReport(2L, 6L);

        assertEquals(2L, response.getStudentId());
        assertEquals(1, response.getWars().size());
        assertEquals("Alex Carter", response.getWars().get(0).getStudentName());
    }

    private User buildStudent(Long id, String firstName, String lastName) {
        User user = new User();
        user.setId(id);
        user.setEmail(firstName.toLowerCase() + "@tcu.edu");
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(UserRole.STUDENT);
        user.setActive(true);
        user.setSection(section);
        user.setTeam(team);
        return user;
    }

    private PeerEvaluation buildEvaluation() {
        Rubric rubric = new Rubric();
        rubric.setId(8L);
        rubric.setName("Peer Rubric");

        RubricCriterion criterion = new RubricCriterion();
        criterion.setId(9L);
        criterion.setName("Contribution");
        criterion.setRubric(rubric);

        PeerEvaluationCriterionScore criterionScore = new PeerEvaluationCriterionScore();
        criterionScore.setId(10L);
        criterionScore.setCriterion(criterion);
        criterionScore.setScore(4);
        criterionScore.setPublicComment("Good teammate");
        criterionScore.setPrivateComment("No concerns");

        PeerEvaluation evaluation = new PeerEvaluation();
        evaluation.setId(11L);
        evaluation.setEvaluator(evaluator);
        evaluation.setEvaluatee(evaluatee);
        evaluation.setTeam(team);
        evaluation.setSection(section);
        evaluation.setActiveWeek(activeWeek);
        evaluation.setRubric(rubric);
        evaluation.setSubmittedAt(LocalDateTime.of(2026, 4, 20, 11, 0));
        evaluation.addCriterionScore(criterionScore);
        return evaluation;
    }

    private WeeklyActivityReport buildWar() {
        WarActivity activity = new WarActivity();
        activity.setId(20L);
        activity.setDescription("Implemented REST endpoint");
        activity.setHoursSpent(new BigDecimal("4.00"));
        activity.setCategory("Backend");

        WeeklyActivityReport war = new WeeklyActivityReport();
        war.setId(21L);
        war.setStudent(evaluatee);
        war.setTeam(team);
        war.setSection(section);
        war.setActiveWeek(activeWeek);
        war.setSubmittedAt(LocalDateTime.of(2026, 4, 20, 9, 0));
        war.setStatus(WarStatus.SUBMITTED);
        war.addActivity(activity);
        return war;
    }
}
