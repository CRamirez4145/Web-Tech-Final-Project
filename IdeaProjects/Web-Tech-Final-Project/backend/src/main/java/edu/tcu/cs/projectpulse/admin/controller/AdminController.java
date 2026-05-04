package edu.tcu.cs.projectpulse.admin.controller;

import edu.tcu.cs.projectpulse.admin.service.AdminManagementService;
import edu.tcu.cs.projectpulse.shared.dto.ActiveWeekRequest;
import edu.tcu.cs.projectpulse.shared.dto.ActiveWeekResponse;
import edu.tcu.cs.projectpulse.shared.dto.RubricRequest;
import edu.tcu.cs.projectpulse.shared.dto.RubricResponse;
import edu.tcu.cs.projectpulse.shared.dto.SectionRequest;
import edu.tcu.cs.projectpulse.shared.dto.SectionResponse;
import edu.tcu.cs.projectpulse.shared.dto.TeamRequest;
import edu.tcu.cs.projectpulse.shared.dto.TeamResponse;
import edu.tcu.cs.projectpulse.shared.dto.UserRequest;
import edu.tcu.cs.projectpulse.shared.dto.UserResponse;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminManagementService adminManagementService;

    public AdminController(AdminManagementService adminManagementService) {
        this.adminManagementService = adminManagementService;
    }

    @PostMapping("/rubrics")
    public ResponseEntity<RubricResponse> createRubric(@Valid @RequestBody RubricRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminManagementService.createRubric(request));
    }

    @GetMapping("/rubrics")
    public ResponseEntity<List<RubricResponse>> getRubrics() {
        return ResponseEntity.ok(adminManagementService.getRubrics());
    }

    @PostMapping("/sections")
    public ResponseEntity<SectionResponse> createSection(@Valid @RequestBody SectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminManagementService.createSection(request));
    }

    @GetMapping("/sections")
    public ResponseEntity<List<SectionResponse>> getSections() {
        return ResponseEntity.ok(adminManagementService.getSections());
    }

    @PutMapping("/sections/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(@PathVariable Long sectionId,
                                                         @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.ok(adminManagementService.updateSection(sectionId, request));
    }

    @PostMapping("/active-weeks")
    public ResponseEntity<ActiveWeekResponse> createActiveWeek(@Valid @RequestBody ActiveWeekRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminManagementService.createActiveWeek(request));
    }

    @GetMapping("/active-weeks")
    public ResponseEntity<List<ActiveWeekResponse>> getActiveWeeks() {
        return ResponseEntity.ok(adminManagementService.getActiveWeeks());
    }

    @PutMapping("/active-weeks/{activeWeekId}")
    public ResponseEntity<ActiveWeekResponse> updateActiveWeek(@PathVariable Long activeWeekId,
                                                               @Valid @RequestBody ActiveWeekRequest request) {
        return ResponseEntity.ok(adminManagementService.updateActiveWeek(activeWeekId, request));
    }

    @PostMapping("/teams")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminManagementService.createTeam(request));
    }

    @GetMapping("/teams")
    public ResponseEntity<List<TeamResponse>> getTeams() {
        return ResponseEntity.ok(adminManagementService.getTeams());
    }

    @PutMapping("/teams/{teamId}")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable Long teamId,
                                                   @Valid @RequestBody TeamRequest request) {
        return ResponseEntity.ok(adminManagementService.updateTeam(teamId, request));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminManagementService.createUser(request));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(required = false) UserRole role,
                                                       @RequestParam(required = false) String search) {
        return ResponseEntity.ok(adminManagementService.getUsers(role, search));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId,
                                                   @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(adminManagementService.updateUser(userId, request));
    }

    @PostMapping("/teams/{teamId}/students/{studentId}")
    public ResponseEntity<TeamResponse> assignStudentToTeam(@PathVariable Long teamId, @PathVariable Long studentId) {
        return ResponseEntity.ok(adminManagementService.assignStudentToTeam(teamId, studentId));
    }

    @DeleteMapping("/teams/{teamId}/students/{studentId}")
    public ResponseEntity<TeamResponse> removeStudentFromTeam(@PathVariable Long teamId, @PathVariable Long studentId) {
        return ResponseEntity.ok(adminManagementService.removeStudentFromTeam(teamId, studentId));
    }

    @PostMapping("/teams/{teamId}/instructors/{instructorId}")
    public ResponseEntity<TeamResponse> assignInstructorToTeam(@PathVariable Long teamId,
                                                               @PathVariable Long instructorId) {
        return ResponseEntity.ok(adminManagementService.assignInstructorToTeam(teamId, instructorId));
    }

    @DeleteMapping("/teams/{teamId}/instructors/{instructorId}")
    public ResponseEntity<TeamResponse> removeInstructorFromTeam(@PathVariable Long teamId,
                                                                 @PathVariable Long instructorId) {
        return ResponseEntity.ok(adminManagementService.removeInstructorFromTeam(teamId, instructorId));
    }
}
