package edu.tcu.cs.projectpulse.admin.student.service;

import edu.tcu.cs.projectpulse.admin.student.dto.InviteStudentsRequest;
import edu.tcu.cs.projectpulse.admin.student.dto.InviteStudentsResponse;
import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminStudentInvitationServiceTest {

    @Mock
    private StudentInvitationSender studentInvitationSender;

    @InjectMocks
    private AdminStudentInvitationService adminStudentInvitationService;

    private InviteStudentsRequest request;

    @BeforeEach
    void setUp() {
        request = new InviteStudentsRequest();
        request.setEmails(List.of(" Ada@TCU.edu ", "grace@tcu.edu"));
    }

    @Test
    @DisplayName("inviteStudents sends invitations and returns preview details")
    void inviteStudentsShouldSendInvitationsAndReturnPreview() {
        InviteStudentsResponse response = adminStudentInvitationService.inviteStudents(request);

        assertEquals(2, response.getInvitedCount());
        assertEquals(List.of("ada@tcu.edu", "grace@tcu.edu"), response.getEmails());
        assertEquals("You're invited to join Project Pulse", response.getSubject());
        assertEquals("""
                Hello,

                You've been invited to join Project Pulse.
                Use your invited email address to create your student account.

                Thanks,
                Project Pulse Admin
                """, response.getMessagePreview());
        verify(studentInvitationSender, times(1)).sendInvitation(
                "ada@tcu.edu",
                "You're invited to join Project Pulse",
                response.getMessagePreview()
        );
        verify(studentInvitationSender, times(1)).sendInvitation(
                "grace@tcu.edu",
                "You're invited to join Project Pulse",
                response.getMessagePreview()
        );
    }

    @Test
    @DisplayName("inviteStudents rejects duplicate emails after normalization")
    void inviteStudentsShouldRejectDuplicateEmails() {
        request.setEmails(List.of("ada@tcu.edu", " ADA@TCU.edu "));

        assertThrows(BusinessRuleException.class, () -> adminStudentInvitationService.inviteStudents(request));
    }
}
