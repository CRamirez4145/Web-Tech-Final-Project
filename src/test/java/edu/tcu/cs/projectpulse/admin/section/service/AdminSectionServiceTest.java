package edu.tcu.cs.projectpulse.admin.section.service;

import edu.tcu.cs.projectpulse.admin.section.dto.CreateSectionRequest;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionDetailResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionSummaryResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SetSectionWeeksRequest;
import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.shared.entity.ActiveWeek;
import edu.tcu.cs.projectpulse.shared.entity.Rubric;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.repository.ActiveWeekRepository;
import edu.tcu.cs.projectpulse.shared.repository.RubricRepository;
import edu.tcu.cs.projectpulse.shared.repository.SectionRepository;
import edu.tcu.cs.projectpulse.shared.repository.TeamRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminSectionServiceTest {

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private ActiveWeekRepository activeWeekRepository;

    @Mock
    private RubricRepository rubricRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminSectionService adminSectionService;

    @Test
    @DisplayName("findSections returns all sections sorted by name when no filter is provided")
    void findSectionsShouldReturnAllSectionsWhenFilterMissing() {
        when(sectionRepository.findAllByOrderByNameAsc()).thenReturn(List.of(section(2L, "Section A"), section(1L, "Section B")));

        List<SectionSummaryResponse> response = adminSectionService.findSections("   ");

        assertEquals(2, response.size());
        assertEquals("Section A", response.get(0).getName());
        assertEquals("Section B", response.get(1).getName());
        verify(sectionRepository).findAllByOrderByNameAsc();
    }

    @Test
    @DisplayName("findSections filters sections by partial name")
    void findSectionsShouldFilterByName() {
        when(sectionRepository.findByNameContainingIgnoreCaseOrderByNameAsc("senior")).thenReturn(List.of(section(3L, "Senior Design 1")));

        List<SectionSummaryResponse> response = adminSectionService.findSections(" senior ");

        assertEquals(1, response.size());
        assertEquals(3L, response.get(0).getId());
        assertEquals("Senior Design 1", response.get(0).getName());
        verify(sectionRepository).findByNameContainingIgnoreCaseOrderByNameAsc("senior");
    }

    @Test
    @DisplayName("createSection saves a section with dates and rubric")
    void createSectionShouldSaveSection() {
        CreateSectionRequest request = new CreateSectionRequest();
        request.setName(" Senior Design A ");
        request.setStartDate(LocalDate.of(2026, 8, 20));
        request.setEndDate(LocalDate.of(2026, 12, 10));
        request.setRubricId(7L);

        Rubric rubric = rubric(7L, "Teamwork Rubric");

        when(sectionRepository.existsByNameIgnoreCase("Senior Design A")).thenReturn(false);
        when(rubricRepository.findById(7L)).thenReturn(Optional.of(rubric));
        when(sectionRepository.save(any(Section.class))).thenAnswer(invocation -> {
            Section section = invocation.getArgument(0);
            section.setId(3L);
            return section;
        });

        SectionDetailResponse response = adminSectionService.createSection(request);

        assertEquals(3L, response.getId());
        assertEquals("Senior Design A", response.getName());
        assertEquals(LocalDate.of(2026, 8, 20), response.getStartDate());
        assertEquals(LocalDate.of(2026, 12, 10), response.getEndDate());
        assertEquals(7L, response.getRubricId());
        assertEquals("Teamwork Rubric", response.getRubricName());
        assertEquals(0, response.getTeams().size());
        assertEquals(0, response.getStudents().size());
    }

    @Test
    @DisplayName("createSection rejects duplicate section names")
    void createSectionShouldRejectDuplicateName() {
        CreateSectionRequest request = new CreateSectionRequest();
        request.setName("Senior Design A");
        request.setStartDate(LocalDate.of(2026, 8, 20));
        request.setEndDate(LocalDate.of(2026, 12, 10));
        request.setRubricId(7L);

        when(sectionRepository.existsByNameIgnoreCase("Senior Design A")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> adminSectionService.createSection(request));
    }

    @Test
    @DisplayName("createSection rejects end dates before start dates")
    void createSectionShouldRejectInvalidDates() {
        CreateSectionRequest request = new CreateSectionRequest();
        request.setName("Senior Design A");
        request.setStartDate(LocalDate.of(2026, 12, 10));
        request.setEndDate(LocalDate.of(2026, 8, 20));
        request.setRubricId(7L);

        when(sectionRepository.existsByNameIgnoreCase("Senior Design A")).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> adminSectionService.createSection(request));
    }

    @Test
    @DisplayName("updateSection updates section details and keeps team/student detail response")
    void updateSectionShouldUpdateSection() {
        CreateSectionRequest request = new CreateSectionRequest();
        request.setName(" Senior Design B ");
        request.setStartDate(LocalDate.of(2026, 8, 25));
        request.setEndDate(LocalDate.of(2026, 12, 12));
        request.setRubricId(8L);

        Section section = section(3L, "Senior Design A");
        section.setStartDate(LocalDate.of(2026, 8, 20));
        section.setEndDate(LocalDate.of(2026, 12, 10));
        section.setRubric(rubric(7L, "Teamwork Rubric"));
        Team team = team(10L, "Team Alpha", section);
        User user = student(20L, "Ada", "Lovelace", "ada@tcu.edu", team, section);
        Rubric updatedRubric = rubric(8L, "Advanced Rubric");

        when(sectionRepository.findById(3L)).thenReturn(Optional.of(section));
        when(sectionRepository.existsByNameIgnoreCaseAndIdNot("Senior Design B", 3L)).thenReturn(false);
        when(rubricRepository.findById(8L)).thenReturn(Optional.of(updatedRubric));
        when(sectionRepository.save(section)).thenReturn(section);
        when(teamRepository.findBySectionIdOrderByNameAsc(3L)).thenReturn(List.of(team));
        when(userRepository.findBySectionIdOrderByLastNameAscFirstNameAsc(3L)).thenReturn(List.of(user));

        SectionDetailResponse response = adminSectionService.updateSection(3L, request);

        assertEquals("Senior Design B", response.getName());
        assertEquals(LocalDate.of(2026, 8, 25), response.getStartDate());
        assertEquals(LocalDate.of(2026, 12, 12), response.getEndDate());
        assertEquals(8L, response.getRubricId());
        assertEquals("Advanced Rubric", response.getRubricName());
        assertEquals(1, response.getTeams().size());
        assertEquals(1, response.getStudents().size());
    }

    @Test
    @DisplayName("updateSection rejects duplicate section names excluding current section")
    void updateSectionShouldRejectDuplicateName() {
        CreateSectionRequest request = new CreateSectionRequest();
        request.setName("Senior Design B");
        request.setStartDate(LocalDate.of(2026, 8, 20));
        request.setEndDate(LocalDate.of(2026, 12, 10));
        request.setRubricId(7L);

        when(sectionRepository.findById(3L)).thenReturn(Optional.of(section(3L, "Senior Design A")));
        when(sectionRepository.existsByNameIgnoreCaseAndIdNot("Senior Design B", 3L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> adminSectionService.updateSection(3L, request));
    }

    @Test
    @DisplayName("updateSection throws when section does not exist")
    void updateSectionShouldThrowWhenMissing() {
        CreateSectionRequest request = new CreateSectionRequest();
        request.setName("Senior Design B");
        request.setStartDate(LocalDate.of(2026, 8, 20));
        request.setEndDate(LocalDate.of(2026, 12, 10));
        request.setRubricId(7L);

        when(sectionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminSectionService.updateSection(99L, request));
    }

    @Test
    @DisplayName("setSectionWeeks saves selected inactive weeks in sorted order")
    void setSectionWeeksShouldSaveSelectedWeeks() {
        Section section = section(3L, "Senior Design 1");
        section.setRubric(rubric(7L, "Teamwork Rubric"));
        ActiveWeek weekTwo = inactiveWeek(12L, 2);
        ActiveWeek weekOne = inactiveWeek(11L, 1);

        SetSectionWeeksRequest request = new SetSectionWeeksRequest();
        request.setWeekIds(List.of(12L, 11L));

        when(sectionRepository.findById(3L)).thenReturn(Optional.of(section));
        when(activeWeekRepository.findAllById(List.of(12L, 11L))).thenReturn(List.of(weekTwo, weekOne));
        when(sectionRepository.save(section)).thenReturn(section);
        when(teamRepository.findBySectionIdOrderByNameAsc(3L)).thenReturn(List.of());
        when(userRepository.findBySectionIdOrderByLastNameAscFirstNameAsc(3L)).thenReturn(List.of());

        SectionDetailResponse response = adminSectionService.setSectionWeeks(3L, request);

        assertEquals(2, response.getSelectedWeeks().size());
        assertEquals(1, response.getSelectedWeeks().get(0).getWeekNumber());
        assertEquals(2, response.getSelectedWeeks().get(1).getWeekNumber());
    }

    @Test
    @DisplayName("setSectionWeeks rejects active weeks")
    void setSectionWeeksShouldRejectActiveWeeks() {
        Section section = section(3L, "Senior Design 1");
        ActiveWeek activeWeek = inactiveWeek(11L, 1);
        activeWeek.setActive(true);

        SetSectionWeeksRequest request = new SetSectionWeeksRequest();
        request.setWeekIds(List.of(11L));

        when(sectionRepository.findById(3L)).thenReturn(Optional.of(section));
        when(activeWeekRepository.findAllById(List.of(11L))).thenReturn(List.of(activeWeek));

        assertThrows(BusinessRuleException.class, () -> adminSectionService.setSectionWeeks(3L, request));
    }

    @Test
    @DisplayName("setSectionWeeks rejects duplicate week ids")
    void setSectionWeeksShouldRejectDuplicateIds() {
        Section section = section(3L, "Senior Design 1");
        SetSectionWeeksRequest request = new SetSectionWeeksRequest();
        request.setWeekIds(List.of(11L, 11L));

        when(sectionRepository.findById(3L)).thenReturn(Optional.of(section));

        assertThrows(BusinessRuleException.class, () -> adminSectionService.setSectionWeeks(3L, request));
    }

    @Test
    @DisplayName("getSection returns section details with teams and students")
    void getSectionShouldReturnDetails() {
        Section section = section(3L, "Senior Design 1");
        section.setStartDate(LocalDate.of(2026, 8, 20));
        section.setEndDate(LocalDate.of(2026, 12, 10));
        section.setRubric(rubric(7L, "Teamwork Rubric"));
        Team team = team(10L, "Team Alpha", section);
        User user = student(20L, "Ada", "Lovelace", "ada@tcu.edu", team, section);

        when(sectionRepository.findById(3L)).thenReturn(Optional.of(section));
        when(teamRepository.findBySectionIdOrderByNameAsc(3L)).thenReturn(List.of(team));
        when(userRepository.findBySectionIdOrderByLastNameAscFirstNameAsc(3L)).thenReturn(List.of(user));

        SectionDetailResponse response = adminSectionService.getSection(3L);

        assertEquals(3L, response.getId());
        assertEquals("Senior Design 1", response.getName());
        assertEquals(LocalDate.of(2026, 8, 20), response.getStartDate());
        assertEquals(LocalDate.of(2026, 12, 10), response.getEndDate());
        assertEquals(7L, response.getRubricId());
        assertEquals("Teamwork Rubric", response.getRubricName());
        assertEquals(1, response.getTeams().size());
        assertEquals("Team Alpha", response.getTeams().get(0).getName());
        assertEquals(1, response.getStudents().size());
        assertEquals("Ada", response.getStudents().get(0).getFirstName());
        assertEquals("Team Alpha", response.getStudents().get(0).getTeamName());
    }

    @Test
    @DisplayName("getSection throws when section does not exist")
    void getSectionShouldThrowWhenMissing() {
        when(sectionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminSectionService.getSection(99L));
    }

    private Section section(Long id, String name) {
        Section section = new Section();
        section.setId(id);
        section.setName(name);
        return section;
    }

    private Rubric rubric(Long id, String name) {
        Rubric rubric = new Rubric();
        rubric.setId(id);
        rubric.setName(name);
        return rubric;
    }

    private ActiveWeek inactiveWeek(Long id, Integer weekNumber) {
        ActiveWeek week = new ActiveWeek();
        week.setId(id);
        week.setWeekNumber(weekNumber);
        week.setStartDate(LocalDate.of(2026, 1, 1).plusWeeks(weekNumber - 1L));
        week.setEndDate(LocalDate.of(2026, 1, 7).plusWeeks(weekNumber - 1L));
        week.setActive(false);
        return week;
    }

    private Team team(Long id, String name, Section section) {
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        team.setSection(section);
        return team;
    }

    private User student(Long id, String firstName, String lastName, String email, Team team, Section section) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setTeam(team);
        user.setSection(section);
        return user;
    }
}
