package edu.tcu.cs.projectpulse.instructor.account.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.instructor.account.dto.InstructorAccountRequest;
import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
import edu.tcu.cs.projectpulse.instructor.management.service.UserManagementMapper;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstructorAccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserManagementMapper userManagementMapper;

    @InjectMocks
    private InstructorAccountService instructorAccountService;

    private InstructorAccountRequest request;
    private User instructor;

    @BeforeEach
    void setUp() {
        Section section = new Section();
        section.setId(3L);
        section.setName("Section 1");

        instructor = new User();
        instructor.setId(10L);
        instructor.setEmail("mentor@tcu.edu");
        instructor.setFirstName("Pending");
        instructor.setLastName("Instructor");
        instructor.setRole(UserRole.INSTRUCTOR);
        instructor.setActive(false);
        instructor.setSection(section);

        request = new InstructorAccountRequest();
        request.setEmail("mentor@tcu.edu");
        request.setFirstName("Jamie");
        request.setLastName("Smith");
    }

    @Test
    @DisplayName("createAccount activates an invited instructor")
    void createAccountShouldActivateInstructor() {
        when(userRepository.findByEmailIgnoreCase("mentor@tcu.edu")).thenReturn(Optional.of(instructor));
        when(userRepository.save(instructor)).thenReturn(instructor);

        ManagedUserResponse response = instructorAccountService.createAccount(request);

        assertEquals("Jamie", response.getFirstName());
        assertEquals(true, response.isActive());
    }

    @Test
    @DisplayName("createAccount rejects invitations that are not instructors")
    void createAccountShouldRejectNonInstructorInvitation() {
        instructor.setRole(UserRole.STUDENT);
        when(userRepository.findByEmailIgnoreCase("mentor@tcu.edu")).thenReturn(Optional.of(instructor));

        assertThrows(BusinessRuleException.class, () -> instructorAccountService.createAccount(request));
    }
}
