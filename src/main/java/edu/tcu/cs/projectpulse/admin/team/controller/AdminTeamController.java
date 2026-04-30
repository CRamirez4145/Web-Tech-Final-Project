package edu.tcu.cs.projectpulse.admin.team.controller;

import edu.tcu.cs.projectpulse.admin.team.dto.AssignStudentsRequest;
import edu.tcu.cs.projectpulse.admin.team.dto.CreateTeamRequest;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamAssignmentResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamDetailResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamSummaryResponse;
import edu.tcu.cs.projectpulse.admin.team.service.AdminTeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class AdminTeamController {

    private final AdminTeamService adminTeamService;

    public AdminTeamController(AdminTeamService adminTeamService) {
        this.adminTeamService = adminTeamService;
    }

    @PostMapping
    public ResponseEntity<TeamDetailResponse> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminTeamService.createTeam(request));
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponse> updateTeam(@PathVariable Long teamId,
                                                         @Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.ok(adminTeamService.updateTeam(teamId, request));
    }

    @PostMapping("/assign")
    public ResponseEntity<TeamAssignmentResponse> assignStudents(@Valid @RequestBody AssignStudentsRequest request) {
        return ResponseEntity.ok(adminTeamService.assignStudents(request));
    }

    @DeleteMapping("/{teamId}/students/{studentId}")
    public ResponseEntity<TeamAssignmentResponse> removeStudentFromTeam(@PathVariable Long teamId,
                                                                        @PathVariable Long studentId) {
        return ResponseEntity.ok(adminTeamService.removeStudentFromTeam(teamId, studentId));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        adminTeamService.deleteTeam(teamId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TeamSummaryResponse>> findTeams(@RequestParam(required = false) Long sectionId,
                                                               @RequestParam(required = false) String name) {
        return ResponseEntity.ok(adminTeamService.findTeams(sectionId, name));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponse> getTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(adminTeamService.getTeam(teamId));
    }
}
