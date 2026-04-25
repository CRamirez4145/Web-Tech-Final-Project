package edu.tcu.cs.projectpulse.instructor.management.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.instructor.management.dto.AssignInstructorTeamRequest;
import edu.tcu.cs.projectpulse.instructor.management.dto.InstructorInvitationRequest;
import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import edu.tcu.cs.projectpulse.shared.repository.SectionRepository;
import edu.tcu.cs.projectpulse.shared.repository.TeamRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstructorManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private TeamRepository teamRepository;

    @Spy
    private UserManagementMapper userManagementMapper;

    @InjectMocks
    private InstructorManagementService instructorManagementService;

    private Section section;
    private Team team;
    private User instructor;

    @BeforeEach
    void setUp() {
        section = new Section();
        section.setId(4L);
        section.setName("Section 1");

        team = new Team();
        team.setId(7L);
        team.setName("Team Beta");
        team.setSection(section);

        instructor = new User();
        instructor.setId(5L);
        instructor.setEmail("mentor@tcu.edu");
        instructor.setFirstName("Jamie");
        instructor.setLastName("Smith");
        instructor.setRole(UserRole.INSTRUCTOR);
        instructor.setActive(true);
        instructor.setSection(section);
    }

    @Test
    @DisplayName("inviteInstructor creates an inactive instructor invitation")
    void inviteInstructorShouldCreatePendingInstructor() {
        InstructorInvitationRequest request = new InstructorInvitationRequest();
        request.setEmail("mentor@tcu.edu");
        request.setSectionId(4L);
        request.setTeamId(7L);

        when(userRepository.findByEmailIgnoreCase("mentor@tcu.edu")).thenReturn(Optional.empty());
        when(sectionRepository.findById(4L)).thenReturn(Optional.of(section));
        when(teamRepository.findById(7L)).thenReturn(Optional.of(team));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        ManagedUserResponse response = instructorManagementService.inviteInstructor(request);

        assertEquals(5L, response.getId());
        assertEquals("INSTRUCTOR", response.getRole());
        assertFalse(response.isActive());
    }

    @Test
    @DisplayName("assignInstructor rejects a team from another section")
    void assignInstructorShouldRejectTeamInDifferentSection() {
        Section otherSection = new Section();
        otherSection.setId(99L);
        otherSection.setName("Other");

        Team otherTeam = new Team();
        otherTeam.setId(8L);
        otherTeam.setName("Team Other");
        otherTeam.setSection(otherSection);

        AssignInstructorTeamRequest request = new AssignInstructorTeamRequest();
        request.setTeamId(8L);

        when(userRepository.findById(5L)).thenReturn(Optional.of(instructor));
        when(teamRepository.findById(8L)).thenReturn(Optional.of(otherTeam));

        assertThrows(BusinessRuleException.class, () -> instructorManagementService.assignInstructor(5L, request));
    }

    @Test
    @DisplayName("reactivateInstructor marks the instructor active")
    void reactivateInstructorShouldActivateInstructor() {
        instructor.setActive(false);
        when(userRepository.findById(5L)).thenReturn(Optional.of(instructor));
        when(userRepository.save(instructor)).thenReturn(instructor);

        ManagedUserResponse response = instructorManagementService.reactivateInstructor(5L);

        assertEquals(true, response.isActive());
    }
}
