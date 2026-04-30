package edu.tcu.cs.projectpulse.admin.team.service;

import edu.tcu.cs.projectpulse.admin.team.dto.AssignStudentsRequest;
import edu.tcu.cs.projectpulse.admin.team.dto.CreateTeamRequest;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamAssignmentResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamDetailResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamSummaryResponse;
import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.repository.SectionRepository;
import edu.tcu.cs.projectpulse.shared.repository.TeamRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.student.peer.repository.PeerEvaluationRepository;
import edu.tcu.cs.projectpulse.student.war.repository.WeeklyActivityReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WeeklyActivityReportRepository weeklyActivityReportRepository;

    @Mock
    private PeerEvaluationRepository peerEvaluationRepository;

    @InjectMocks
    private AdminTeamService adminTeamService;

    private CreateTeamRequest createRequest;
    private AssignStudentsRequest assignStudentsRequest;

    @BeforeEach
    void setUp() {
        createRequest = new CreateTeamRequest();
        createRequest.setName(" Alpha ");
        createRequest.setDescription(" Platform team ");
        createRequest.setWebsite("https://alpha.example.com");
        createRequest.setSectionId(3L);

        assignStudentsRequest = new AssignStudentsRequest();
        assignStudentsRequest.setTeamId(1L);
        assignStudentsRequest.setStudentIds(List.of(10L, 11L));
    }

    @Test
    @DisplayName("createTeam saves the team with normalized fields")
    void createTeamShouldSaveTeam() {
        Section section = section(3L, "Section A");

        when(teamRepository.existsByNameIgnoreCase("Alpha")).thenReturn(false);
        when(sectionRepository.findById(3L)).thenReturn(Optional.of(section));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> {
            Team team = invocation.getArgument(0);
            team.setId(1L);
            return team;
        });

        TeamDetailResponse response = adminTeamService.createTeam(createRequest);

        assertEquals(1L, response.getId());
        assertEquals("Alpha", response.getName());
        assertEquals("Platform team", response.getDescription());
        assertEquals("https://alpha.example.com", response.getWebsite());
        assertEquals(3L, response.getSectionId());
        assertEquals("Section A", response.getSectionName());
        assertEquals(0, response.getStudents().size());
    }

    @Test
    @DisplayName("createTeam stores blank optional fields as null")
    void createTeamShouldNormalizeBlankOptionalFields() {
        Section section = section(3L, "Section A");
        createRequest.setDescription("   ");
        createRequest.setWebsite("   ");

        when(teamRepository.existsByNameIgnoreCase("Alpha")).thenReturn(false);
        when(sectionRepository.findById(3L)).thenReturn(Optional.of(section));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> {
            Team team = invocation.getArgument(0);
            team.setId(1L);
            return team;
        });

        TeamDetailResponse response = adminTeamService.createTeam(createRequest);

        assertNull(response.getDescription());
        assertNull(response.getWebsite());
    }

    @Test
    @DisplayName("createTeam rejects duplicate team names")
    void createTeamShouldRejectDuplicateName() {
        when(teamRepository.existsByNameIgnoreCase("Alpha")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> adminTeamService.createTeam(createRequest));
    }

    @Test
    @DisplayName("createTeam throws when section does not exist")
    void createTeamShouldThrowWhenSectionMissing() {
        when(teamRepository.existsByNameIgnoreCase("Alpha")).thenReturn(false);
        when(sectionRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminTeamService.createTeam(createRequest));
    }

    @Test
    @DisplayName("updateTeam reuses the create request fields and keeps students in the response")
    void updateTeamShouldReuseCreateRequest() {
        Section originalSection = section(3L, "Section A");
        Section updatedSection = section(4L, "Section B");
        Team team = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");
        User student = student(10L, "Ada", "Lovelace", "ada@tcu.edu", team);

        createRequest.setName(" Beta ");
        createRequest.setDescription(" Product team ");
        createRequest.setWebsite("https://beta.example.com");
        createRequest.setSectionId(4L);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamRepository.existsByNameIgnoreCaseAndIdNot("Beta", 1L)).thenReturn(false);
        when(sectionRepository.findById(4L)).thenReturn(Optional.of(updatedSection));
        when(teamRepository.save(team)).thenReturn(team);
        when(userRepository.findByTeamIdOrderByLastNameAscFirstNameAsc(1L)).thenReturn(List.of(student));

        TeamDetailResponse response = adminTeamService.updateTeam(1L, createRequest);

        assertEquals(1L, response.getId());
        assertEquals("Beta", response.getName());
        assertEquals("Product team", response.getDescription());
        assertEquals("https://beta.example.com", response.getWebsite());
        assertEquals(4L, response.getSectionId());
        assertEquals("Section B", response.getSectionName());
        assertEquals(1, response.getStudents().size());
    }

    @Test
    @DisplayName("updateTeam rejects duplicate team names excluding current team")
    void updateTeamShouldRejectDuplicateName() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A")));
        when(teamRepository.existsByNameIgnoreCaseAndIdNot("Alpha", 1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> adminTeamService.updateTeam(1L, createRequest));
    }

    @Test
    @DisplayName("updateTeam throws when the team does not exist")
    void updateTeamShouldThrowWhenMissing() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminTeamService.updateTeam(99L, createRequest));
    }

    @Test
    @DisplayName("updateTeam throws when the new section does not exist")
    void updateTeamShouldThrowWhenSectionMissing() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A")));
        when(teamRepository.existsByNameIgnoreCaseAndIdNot("Alpha", 1L)).thenReturn(false);
        when(sectionRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminTeamService.updateTeam(1L, createRequest));
    }

    @Test
    @DisplayName("assignStudents reassigns students and returns updated section state")
    void assignStudentsShouldReassignStudents() {
        Section section = section(3L, "Section A");
        Team targetTeam = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");
        Team otherTeam = team(2L, "Beta", "Product team", "https://beta.example.com", 3L, "Section A");
        User ada = student(10L, "Ada", "Lovelace", "ada@tcu.edu", otherTeam);
        User grace = student(11L, "Grace", "Hopper", "grace@tcu.edu", otherTeam);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(targetTeam));
        when(userRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(ada, grace));
        when(userRepository.saveAll(List.of(ada, grace))).thenReturn(List.of(ada, grace));
        when(teamRepository.findBySectionIdOrderByNameAsc(3L)).thenReturn(List.of(targetTeam, otherTeam));
        when(userRepository.findBySectionIdOrderByLastNameAscFirstNameAsc(3L)).thenReturn(List.of(grace, ada));

        TeamAssignmentResponse response = adminTeamService.assignStudents(assignStudentsRequest);

        assertEquals(3L, response.getSectionId());
        assertEquals("Section A", response.getSectionName());
        assertEquals(2, response.getTeams().size());
        assertEquals(2, response.getTeams().get(0).getStudentCount());
        assertEquals(0, response.getTeams().get(1).getStudentCount());
        assertEquals(2, response.getStudents().size());
        assertEquals(1L, ada.getTeam().getId());
        assertEquals(1L, grace.getTeam().getId());
        assertEquals("Alpha", response.getStudents().get(0).getTeamName());
    }

    @Test
    @DisplayName("assignStudents rejects duplicate student ids")
    void assignStudentsShouldRejectDuplicateStudentIds() {
        assignStudentsRequest.setStudentIds(List.of(10L, 10L));

        assertThrows(BusinessRuleException.class, () -> adminTeamService.assignStudents(assignStudentsRequest));
    }

    @Test
    @DisplayName("assignStudents throws when team does not exist")
    void assignStudentsShouldThrowWhenTeamMissing() {
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminTeamService.assignStudents(assignStudentsRequest));
    }

    @Test
    @DisplayName("assignStudents throws when a student does not exist")
    void assignStudentsShouldThrowWhenStudentMissing() {
        Team targetTeam = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");
        User ada = student(10L, "Ada", "Lovelace", "ada@tcu.edu", targetTeam);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(targetTeam));
        when(userRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(ada));

        assertThrows(ResourceNotFoundException.class, () -> adminTeamService.assignStudents(assignStudentsRequest));
    }

    @Test
    @DisplayName("assignStudents rejects students from another section")
    void assignStudentsShouldRejectOtherSectionStudents() {
        Section sectionA = section(3L, "Section A");
        Section sectionB = section(4L, "Section B");
        Team targetTeam = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");
        Team otherSectionTeam = team(9L, "Gamma", "QA team", "https://gamma.example.com", 4L, "Section B");
        User ada = student(10L, "Ada", "Lovelace", "ada@tcu.edu", otherSectionTeam);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(targetTeam));
        when(userRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(
                ada,
                student(11L, "Grace", "Hopper", "grace@tcu.edu", targetTeam)
        ));

        assertThrows(BusinessRuleException.class, () -> adminTeamService.assignStudents(assignStudentsRequest));
    }

    @Test
    @DisplayName("removeStudentFromTeam clears the team and returns updated assignment state")
    void removeStudentFromTeamShouldClearTeam() {
        Team targetTeam = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");
        Team otherTeam = team(2L, "Beta", "Product team", "https://beta.example.com", 3L, "Section A");
        User removedStudent = student(10L, "Ada", "Lovelace", "ada@tcu.edu", targetTeam);
        User remainingStudent = student(11L, "Grace", "Hopper", "grace@tcu.edu", targetTeam);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(targetTeam));
        when(userRepository.findById(10L)).thenReturn(Optional.of(removedStudent));
        when(userRepository.save(removedStudent)).thenAnswer(invocation -> invocation.getArgument(0));
        when(teamRepository.findBySectionIdOrderByNameAsc(3L)).thenReturn(List.of(targetTeam, otherTeam));
        when(userRepository.findBySectionIdOrderByLastNameAscFirstNameAsc(3L)).thenReturn(List.of(remainingStudent, removedStudent));

        TeamAssignmentResponse response = adminTeamService.removeStudentFromTeam(1L, 10L);

        assertEquals(3L, response.getSectionId());
        assertEquals(1, response.getTeams().get(0).getStudentCount());
        assertEquals(0, response.getTeams().get(1).getStudentCount());
        assertEquals(10L, response.getStudents().get(1).getId());
        assertNull(removedStudent.getTeam());
        assertNull(response.getStudents().get(1).getTeamId());
        assertNull(response.getStudents().get(1).getTeamName());
    }

    @Test
    @DisplayName("removeStudentFromTeam throws when the student is not on the requested team")
    void removeStudentFromTeamShouldRejectWrongTeam() {
        Team targetTeam = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");
        Team otherTeam = team(2L, "Beta", "Product team", "https://beta.example.com", 3L, "Section A");
        User student = student(10L, "Ada", "Lovelace", "ada@tcu.edu", otherTeam);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(targetTeam));
        when(userRepository.findById(10L)).thenReturn(Optional.of(student));

        assertThrows(BusinessRuleException.class, () -> adminTeamService.removeStudentFromTeam(1L, 10L));
    }

    @Test
    @DisplayName("removeStudentFromTeam throws when student is already unassigned")
    void removeStudentFromTeamShouldRejectUnassignedStudent() {
        Team targetTeam = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");
        User student = student(10L, "Ada", "Lovelace", "ada@tcu.edu", targetTeam);
        student.setTeam(null);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(targetTeam));
        when(userRepository.findById(10L)).thenReturn(Optional.of(student));

        assertThrows(BusinessRuleException.class, () -> adminTeamService.removeStudentFromTeam(1L, 10L));
    }

    @Test
    @DisplayName("removeStudentFromTeam throws when team is missing")
    void removeStudentFromTeamShouldThrowWhenTeamMissing() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminTeamService.removeStudentFromTeam(99L, 10L));
    }

    @Test
    @DisplayName("removeStudentFromTeam throws when student is missing")
    void removeStudentFromTeamShouldThrowWhenStudentMissing() {
        Team targetTeam = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");

        when(teamRepository.findById(1L)).thenReturn(Optional.of(targetTeam));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminTeamService.removeStudentFromTeam(1L, 99L));
    }

    @Test
    @DisplayName("deleteTeam deletes an empty team with no dependent records")
    void deleteTeamShouldDeleteEmptyTeam() {
        Team team = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.existsByTeamId(1L)).thenReturn(false);
        when(weeklyActivityReportRepository.existsByTeamId(1L)).thenReturn(false);
        when(peerEvaluationRepository.existsByTeamId(1L)).thenReturn(false);
        doNothing().when(teamRepository).delete(team);

        adminTeamService.deleteTeam(1L);

        verify(teamRepository).delete(team);
    }

    @Test
    @DisplayName("deleteTeam rejects teams with assigned students")
    void deleteTeamShouldRejectAssignedStudents() {
        Team team = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.existsByTeamId(1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> adminTeamService.deleteTeam(1L));
    }

    @Test
    @DisplayName("deleteTeam rejects teams referenced by weekly activity reports")
    void deleteTeamShouldRejectWeeklyActivityReports() {
        Team team = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.existsByTeamId(1L)).thenReturn(false);
        when(weeklyActivityReportRepository.existsByTeamId(1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> adminTeamService.deleteTeam(1L));
    }

    @Test
    @DisplayName("deleteTeam rejects teams referenced by peer evaluations")
    void deleteTeamShouldRejectPeerEvaluations() {
        Team team = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.existsByTeamId(1L)).thenReturn(false);
        when(weeklyActivityReportRepository.existsByTeamId(1L)).thenReturn(false);
        when(peerEvaluationRepository.existsByTeamId(1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> adminTeamService.deleteTeam(1L));
    }

    @Test
    @DisplayName("deleteTeam throws when the team does not exist")
    void deleteTeamShouldThrowWhenMissing() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminTeamService.deleteTeam(99L));
    }

    @Test
    @DisplayName("findTeams returns all teams sorted by name when no filters are provided")
    void findTeamsShouldReturnAllTeamsWhenFiltersMissing() {
        when(teamRepository.findAllByOrderByNameAsc()).thenReturn(List.of(team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A")));

        List<TeamSummaryResponse> response = adminTeamService.findTeams(null, "   ");

        assertEquals(1, response.size());
        assertEquals("Alpha", response.get(0).getName());
        assertEquals("Platform team", response.get(0).getDescription());
        verify(teamRepository).findAllByOrderByNameAsc();
    }

    @Test
    @DisplayName("findTeams filters by section")
    void findTeamsShouldFilterBySection() {
        when(teamRepository.findBySectionIdOrderByNameAsc(3L)).thenReturn(List.of(team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A")));

        List<TeamSummaryResponse> response = adminTeamService.findTeams(3L, null);

        assertEquals(1, response.size());
        assertEquals(3L, response.get(0).getSectionId());
        verify(teamRepository).findBySectionIdOrderByNameAsc(3L);
    }

    @Test
    @DisplayName("findTeams filters by name")
    void findTeamsShouldFilterByName() {
        when(teamRepository.findByNameContainingIgnoreCaseOrderByNameAsc("alp")).thenReturn(List.of(team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A")));

        List<TeamSummaryResponse> response = adminTeamService.findTeams(null, " alp ");

        assertEquals(1, response.size());
        assertEquals("Alpha", response.get(0).getName());
        verify(teamRepository).findByNameContainingIgnoreCaseOrderByNameAsc("alp");
    }

    @Test
    @DisplayName("findTeams filters by section and name together")
    void findTeamsShouldFilterBySectionAndName() {
        when(teamRepository.findBySectionIdAndNameContainingIgnoreCaseOrderByNameAsc(3L, "alp"))
                .thenReturn(List.of(team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A")));

        List<TeamSummaryResponse> response = adminTeamService.findTeams(3L, " alp ");

        assertEquals(1, response.size());
        assertEquals("Section A", response.get(0).getSectionName());
        verify(teamRepository).findBySectionIdAndNameContainingIgnoreCaseOrderByNameAsc(3L, "alp");
    }

    @Test
    @DisplayName("getTeam returns team details with students")
    void getTeamShouldReturnTeamDetails() {
        Team team = team(1L, "Alpha", "Platform team", "https://alpha.example.com", 3L, "Section A");
        User student = student(10L, "Ada", "Lovelace", "ada@tcu.edu", team);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findByTeamIdOrderByLastNameAscFirstNameAsc(1L)).thenReturn(List.of(student));

        TeamDetailResponse response = adminTeamService.getTeam(1L);

        assertEquals(1L, response.getId());
        assertEquals("Alpha", response.getName());
        assertEquals("Platform team", response.getDescription());
        assertEquals("https://alpha.example.com", response.getWebsite());
        assertEquals(3L, response.getSectionId());
        assertEquals("Section A", response.getSectionName());
        assertEquals(1, response.getStudents().size());
        assertEquals("Ada", response.getStudents().get(0).getFirstName());
        assertEquals("ada@tcu.edu", response.getStudents().get(0).getEmail());
    }

    @Test
    @DisplayName("getTeam throws when team does not exist")
    void getTeamShouldThrowWhenMissing() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminTeamService.getTeam(99L));
    }

    private Team team(Long id, String name, String description, String website, Long sectionId, String sectionName) {
        Section section = new Section();
        section.setId(sectionId);
        section.setName(sectionName);

        Team team = new Team();
        team.setId(id);
        team.setName(name);
        team.setDescription(description);
        team.setWebsite(website);
        team.setSection(section);
        return team;
    }

    private Section section(Long id, String name) {
        Section section = new Section();
        section.setId(id);
        section.setName(name);
        return section;
    }

    private User student(Long id, String firstName, String lastName, String email, Team team) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setTeam(team);
        user.setSection(team.getSection());
        return user;
    }
}
