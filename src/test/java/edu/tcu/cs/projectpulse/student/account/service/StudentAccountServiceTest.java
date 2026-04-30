package edu.tcu.cs.projectpulse.student.account.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.repository.SectionRepository;
import edu.tcu.cs.projectpulse.shared.repository.TeamRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.student.account.dto.StudentAccountRequest;
import edu.tcu.cs.projectpulse.student.account.dto.StudentAccountResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentAccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private SectionRepository sectionRepository;

    @InjectMocks
    private StudentAccountService studentAccountService;

    private StudentAccountRequest request;
    private Team team;
    private Section section;
    private User user;

    @BeforeEach
    void setUp() {
        section = new Section();
        section.setId(3L);
        section.setName("Section 1");

        team = new Team();
        team.setId(2L);
        team.setName("Team Alpha");
        team.setSection(section);

        user = new User();
        user.setId(1L);
        user.setEmail("javier@tcu.edu");
        user.setFirstName("Javier");
        user.setLastName("Lopez");
        user.setTeam(team);
        user.setSection(section);

        request = new StudentAccountRequest();
        request.setEmail("javier@tcu.edu");
        request.setFirstName(" Javier ");
        request.setLastName(" Lopez ");
        request.setTeamId(2L);
        request.setSectionId(3L);
    }

    @Test
    @DisplayName("createAccount saves a new student account")
    void createAccountShouldSaveStudent() {
        when(userRepository.findByEmailIgnoreCase("javier@tcu.edu")).thenReturn(Optional.empty());
        when(sectionRepository.findById(3L)).thenReturn(Optional.of(section));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(team));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        StudentAccountResponse response = studentAccountService.createAccount(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("javier@tcu.edu", response.getEmail());
        assertEquals("Javier", response.getFirstName());
    }

    @Test
    @DisplayName("createAccount rejects duplicate email addresses")
    void createAccountShouldRejectDuplicateEmail() {
        when(userRepository.findByEmailIgnoreCase("javier@tcu.edu")).thenReturn(Optional.of(user));

        assertThrows(BusinessRuleException.class, () -> studentAccountService.createAccount(request));
    }

    @Test
    @DisplayName("createAccount rejects a team from a different section")
    void createAccountShouldRejectWrongSectionTeamPair() {
        Section otherSection = new Section();
        otherSection.setId(99L);
        otherSection.setName("Section 99");
        team.setSection(otherSection);

        when(userRepository.findByEmailIgnoreCase("javier@tcu.edu")).thenReturn(Optional.empty());
        when(sectionRepository.findById(3L)).thenReturn(Optional.of(section));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(team));

        assertThrows(BusinessRuleException.class, () -> studentAccountService.createAccount(request));
    }

    @Test
    @DisplayName("getAccount returns the student account")
    void getAccountShouldReturnStudent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        StudentAccountResponse response = studentAccountService.getAccount(1L);

        assertEquals(1L, response.getId());
        assertEquals("Team Alpha", response.getTeamName());
    }

    @Test
    @DisplayName("updateAccount updates an existing student account")
    void updateAccountShouldUpdateStudent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmailIgnoreCase("javier@tcu.edu")).thenReturn(Optional.of(user));
        when(sectionRepository.findById(3L)).thenReturn(Optional.of(section));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(team));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentAccountResponse response = studentAccountService.updateAccount(1L, request);

        assertEquals(1L, response.getId());
        assertEquals("Lopez", response.getLastName());
    }

    @Test
    @DisplayName("getAccount throws when student does not exist")
    void getAccountShouldThrowWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentAccountService.getAccount(99L));
    }
}
