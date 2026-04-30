package edu.tcu.cs.projectpulse.admin.student.service;

public interface StudentInvitationSender {

    void sendInvitation(String email, String subject, String message);
}
