package edu.tcu.cs.projectpulse.instructor.management.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.student.peer.repository.PeerEvaluationRepository;
import edu.tcu.cs.projectpulse.student.war.repository.WeeklyActivityReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WeeklyActivityReportRepository weeklyActivityReportRepository;

    @Mock
    private PeerEvaluationRepository peerEvaluationRepository;

    @Spy
    private UserManagementMapper userManagementMapper;

    @InjectMocks
    private StudentManagementService studentManagementService;

    private User student;

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
        student.setFirstName("Alex");
        student.setLastName("Carter");
        student.setRole(UserRole.STUDENT);
        student.setActive(true);
        student.setSection(section);
        student.setTeam(team);
    }

    @Test
    @DisplayName("findStudents returns mapped student summaries")
    void findStudentsShouldReturnMappedResponses() {
        when(userRepository.searchUsers(UserRole.STUDENT, true, 3L, null, "%alex%")).thenReturn(List.of(student));

        List<ManagedUserResponse> responses = studentManagementService.findStudents("alex", 3L, null, true);

        assertEquals(1, responses.size());
        assertEquals("Alex", responses.get(0).getFirstName());
        assertEquals("STUDENT", responses.get(0).getRole());
    }

    @Test
    @DisplayName("deleteStudent rejects students with historical submissions")
    void deleteStudentShouldRejectWhenHistoryExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(weeklyActivityReportRepository.countByStudentId(1L)).thenReturn(1L);

        assertThrows(BusinessRuleException.class, () -> studentManagementService.deleteStudent(1L));
    }

    @Test
    @DisplayName("deleteStudent removes a student with no dependent records")
    void deleteStudentShouldDeleteWhenNoHistoryExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(weeklyActivityReportRepository.countByStudentId(1L)).thenReturn(0L);
        when(peerEvaluationRepository.countByEvaluatorIdOrEvaluateeId(1L, 1L)).thenReturn(0L);

        studentManagementService.deleteStudent(1L);

        verify(userRepository).delete(student);
    }
}
