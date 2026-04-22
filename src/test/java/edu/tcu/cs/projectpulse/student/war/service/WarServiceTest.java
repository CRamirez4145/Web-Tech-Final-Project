package edu.tcu.cs.projectpulse.student.war.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.shared.entity.ActiveWeek;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.repository.ActiveWeekRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.student.war.dto.CreateWarRequest;
import edu.tcu.cs.projectpulse.student.war.dto.WarActivityRequest;
import edu.tcu.cs.projectpulse.student.war.dto.WarResponse;
import edu.tcu.cs.projectpulse.student.war.entity.WarStatus;
import edu.tcu.cs.projectpulse.student.war.entity.WeeklyActivityReport;
import edu.tcu.cs.projectpulse.student.war.repository.WeeklyActivityReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WarServiceTest {

    @Mock
    private WeeklyActivityReportRepository weeklyActivityReportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActiveWeekRepository activeWeekRepository;

    @InjectMocks
    private WarService warService;

    private User student;
    private ActiveWeek activeWeek;
    private CreateWarRequest createWarRequest;

    @BeforeEach
    void setUp() {
        Section section = new Section();
        section.setId(3L);
        section.setName("Section 1");

        Team team = new Team();
        team.setId(2L);
        team.setName("Team Alpha");
        team.setSection(section);

        student = new User();
        student.setId(1L);
        student.setEmail("student@tcu.edu");
        student.setFirstName("Javier");
        student.setLastName("Lopez");
        student.setTeam(team);
        student.setSection(section);

        activeWeek = new ActiveWeek();
        activeWeek.setId(4L);
        activeWeek.setWeekNumber(7);
        activeWeek.setStartDate(LocalDate.of(2026, 4, 13));
        activeWeek.setEndDate(LocalDate.of(2026, 4, 19));
        activeWeek.setActive(true);

        WarActivityRequest activityRequest = new WarActivityRequest();
        activityRequest.setDescription("Implemented API endpoint");
        activityRequest.setHoursSpent(new BigDecimal("4.00"));
        activityRequest.setCategory("Backend");

        createWarRequest = new CreateWarRequest();
        createWarRequest.setActiveWeekId(4L);
        createWarRequest.setActivities(List.of(activityRequest));
    }

    @Test
    @DisplayName("createWar saves a submitted WAR for the student")
    void createWarShouldSaveReport() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(activeWeekRepository.findById(4L)).thenReturn(Optional.of(activeWeek));
        when(weeklyActivityReportRepository.existsByStudentIdAndActiveWeekId(1L, 4L)).thenReturn(false);
        when(weeklyActivityReportRepository.save(any(WeeklyActivityReport.class))).thenAnswer(invocation -> {
            WeeklyActivityReport report = invocation.getArgument(0);
            report.setId(10L);
            report.getActivities().get(0).setId(20L);
            return report;
        });

        WarResponse response = warService.createWar(1L, createWarRequest);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("SUBMITTED", response.getStatus());
        assertEquals(1, response.getActivities().size());

        ArgumentCaptor<WeeklyActivityReport> captor = ArgumentCaptor.forClass(WeeklyActivityReport.class);
        verify(weeklyActivityReportRepository).save(captor.capture());
        WeeklyActivityReport savedReport = captor.getValue();
        assertEquals(WarStatus.SUBMITTED, savedReport.getStatus());
        assertNotNull(savedReport.getSubmittedAt());
        assertEquals(1L, savedReport.getStudent().getId());
    }

    @Test
    @DisplayName("createWar rejects duplicate submissions for the same week")
    void createWarShouldRejectDuplicateSubmission() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(activeWeekRepository.findById(4L)).thenReturn(Optional.of(activeWeek));
        when(weeklyActivityReportRepository.existsByStudentIdAndActiveWeekId(1L, 4L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> warService.createWar(1L, createWarRequest));
    }

    @Test
    @DisplayName("createWar rejects non-active weeks")
    void createWarShouldRejectInactiveWeek() {
        activeWeek.setActive(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(activeWeekRepository.findById(4L)).thenReturn(Optional.of(activeWeek));

        assertThrows(BusinessRuleException.class, () -> warService.createWar(1L, createWarRequest));
    }

    @Test
    @DisplayName("getWarsForStudent returns the student's WAR list")
    void getWarsForStudentShouldReturnList() {
        WeeklyActivityReport report = buildReport();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(weeklyActivityReportRepository.findByStudentIdOrderBySubmittedAtDesc(1L)).thenReturn(List.of(report));

        List<WarResponse> responses = warService.getWarsForStudent(1L);

        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getId());
    }

    @Test
    @DisplayName("getWarForStudent returns one WAR when it belongs to the student")
    void getWarForStudentShouldReturnReport() {
        WeeklyActivityReport report = buildReport();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(weeklyActivityReportRepository.findByIdAndStudentId(10L, 1L)).thenReturn(Optional.of(report));

        WarResponse response = warService.getWarForStudent(1L, 10L);

        assertEquals(10L, response.getId());
        assertEquals(7, response.getWeekNumber());
    }

    @Test
    @DisplayName("getWarForStudent throws when WAR is missing")
    void getWarForStudentShouldThrowWhenNotFound() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(weeklyActivityReportRepository.findByIdAndStudentId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> warService.getWarForStudent(1L, 99L));
    }

    private WeeklyActivityReport buildReport() {
        WeeklyActivityReport report = new WeeklyActivityReport();
        report.setId(10L);
        report.setStudent(student);
        report.setTeam(student.getTeam());
        report.setSection(student.getSection());
        report.setActiveWeek(activeWeek);
        report.setSubmittedAt(LocalDateTime.of(2026, 4, 20, 9, 0));
        report.setStatus(WarStatus.SUBMITTED);

        edu.tcu.cs.projectpulse.student.war.entity.WarActivity activity =
                new edu.tcu.cs.projectpulse.student.war.entity.WarActivity();
        activity.setId(20L);
        activity.setDescription("Implemented API endpoint");
        activity.setHoursSpent(new BigDecimal("4.00"));
        activity.setCategory("Backend");
        report.addActivity(activity);

        return report;
    }
}
