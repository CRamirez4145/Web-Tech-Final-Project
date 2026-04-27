package edu.tcu.cs.projectpulse.admin.student.controller;

import edu.tcu.cs.projectpulse.admin.student.dto.InviteStudentsRequest;
import edu.tcu.cs.projectpulse.admin.student.dto.InviteStudentsResponse;
import edu.tcu.cs.projectpulse.admin.student.service.AdminStudentInvitationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class AdminStudentInvitationController {

    private final AdminStudentInvitationService adminStudentInvitationService;

    public AdminStudentInvitationController(AdminStudentInvitationService adminStudentInvitationService) {
        this.adminStudentInvitationService = adminStudentInvitationService;
    }

    @PostMapping("/invite")
    public ResponseEntity<InviteStudentsResponse> inviteStudents(@Valid @RequestBody InviteStudentsRequest request) {
        return ResponseEntity.ok(adminStudentInvitationService.inviteStudents(request));
    }
}
