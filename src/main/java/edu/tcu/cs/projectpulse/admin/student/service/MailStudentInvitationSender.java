package edu.tcu.cs.projectpulse.admin.student.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailStudentInvitationSender implements StudentInvitationSender {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public MailStudentInvitationSender(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    @Override
    public void sendInvitation(String email, String subject, String message) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new BusinessRuleException("Email sending is not configured.");
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }
}
