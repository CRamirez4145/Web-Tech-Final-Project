package edu.tcu.cs.projectpulse.admin.student.service;

import edu.tcu.cs.projectpulse.admin.student.dto.InviteStudentsRequest;
import edu.tcu.cs.projectpulse.admin.student.dto.InviteStudentsResponse;
import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AdminStudentInvitationService {

    private static final String INVITATION_SUBJECT = "You're invited to join Project Pulse";
    private static final String INVITATION_MESSAGE = """
            Hello,

            You've been invited to join Project Pulse.
            Use your invited email address to create your student account.

            Thanks,
            Project Pulse Admin
            """;

    private final StudentInvitationSender studentInvitationSender;

    public AdminStudentInvitationService(StudentInvitationSender studentInvitationSender) {
        this.studentInvitationSender = studentInvitationSender;
    }

    @Transactional
    public InviteStudentsResponse inviteStudents(InviteStudentsRequest request) {
        List<String> normalizedEmails = normalizeEmails(request.getEmails());
        validateUniqueEmails(normalizedEmails);

        for (String email : normalizedEmails) {
            studentInvitationSender.sendInvitation(email, INVITATION_SUBJECT, INVITATION_MESSAGE);
        }

        InviteStudentsResponse response = new InviteStudentsResponse();
        response.setEmails(normalizedEmails);
        response.setInvitedCount(normalizedEmails.size());
        response.setSubject(INVITATION_SUBJECT);
        response.setMessagePreview(INVITATION_MESSAGE);
        return response;
    }

    private List<String> normalizeEmails(List<String> emails) {
        return emails.stream()
                .map(email -> email.trim().toLowerCase(Locale.ROOT))
                .toList();
    }

    private void validateUniqueEmails(List<String> emails) {
        Set<String> uniqueEmails = new HashSet<>();
        for (String email : emails) {
            if (!uniqueEmails.add(email)) {
                throw new BusinessRuleException("Invitation emails must be unique.");
            }
        }
    }
}
