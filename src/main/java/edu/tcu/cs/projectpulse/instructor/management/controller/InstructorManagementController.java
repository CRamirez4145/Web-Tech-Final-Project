package edu.tcu.cs.projectpulse.instructor.management.controller;

import edu.tcu.cs.projectpulse.instructor.management.dto.AssignInstructorTeamRequest;
import edu.tcu.cs.projectpulse.instructor.management.dto.InstructorInvitationRequest;
import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
import edu.tcu.cs.projectpulse.instructor.management.service.InstructorManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/instructor/instructors")
public class InstructorManagementController {

    private final InstructorManagementService instructorManagementService;

    public InstructorManagementController(InstructorManagementService instructorManagementService) {
        this.instructorManagementService = instructorManagementService;
    }

    @PostMapping("/invitations")
    public ResponseEntity<ManagedUserResponse> inviteInstructor(@Valid @RequestBody InstructorInvitationRequest request) {
        ManagedUserResponse response = instructorManagementService.inviteInstructor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{instructorId}/team")
    public ResponseEntity<ManagedUserResponse> assignInstructor(@PathVariable Long instructorId,
                                                                @Valid @RequestBody AssignInstructorTeamRequest request) {
        return ResponseEntity.ok(instructorManagementService.assignInstructor(instructorId, request));
    }

    @DeleteMapping("/{instructorId}/team")
    public ResponseEntity<ManagedUserResponse> removeInstructor(@PathVariable Long instructorId) {
        return ResponseEntity.ok(instructorManagementService.removeInstructor(instructorId));
    }

    @GetMapping
    public ResponseEntity<List<ManagedUserResponse>> findInstructors(@RequestParam(required = false) String search,
                                                                     @RequestParam(required = false) Long sectionId,
                                                                     @RequestParam(required = false) Long teamId,
                                                                     @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(instructorManagementService.findInstructors(search, sectionId, teamId, active));
    }

    @GetMapping("/{instructorId}")
    public ResponseEntity<ManagedUserResponse> getInstructor(@PathVariable Long instructorId) {
        return ResponseEntity.ok(instructorManagementService.getInstructor(instructorId));
    }

    @PatchMapping("/{instructorId}/deactivate")
    public ResponseEntity<ManagedUserResponse> deactivateInstructor(@PathVariable Long instructorId) {
        return ResponseEntity.ok(instructorManagementService.deactivateInstructor(instructorId));
    }

    @PatchMapping("/{instructorId}/reactivate")
    public ResponseEntity<ManagedUserResponse> reactivateInstructor(@PathVariable Long instructorId) {
        return ResponseEntity.ok(instructorManagementService.reactivateInstructor(instructorId));
    }
}
