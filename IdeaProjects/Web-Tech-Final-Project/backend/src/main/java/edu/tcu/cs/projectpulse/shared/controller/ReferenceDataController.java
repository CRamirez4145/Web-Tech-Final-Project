package edu.tcu.cs.projectpulse.shared.controller;

import edu.tcu.cs.projectpulse.shared.dto.ActiveWeekResponse;
import edu.tcu.cs.projectpulse.shared.dto.ReferenceDataResponse;
import edu.tcu.cs.projectpulse.shared.dto.RubricResponse;
import edu.tcu.cs.projectpulse.shared.dto.SectionResponse;
import edu.tcu.cs.projectpulse.shared.dto.TeamResponse;
import edu.tcu.cs.projectpulse.shared.dto.UserResponse;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import edu.tcu.cs.projectpulse.shared.service.SharedDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shared")
public class ReferenceDataController {

    private final SharedDataService sharedDataService;

    public ReferenceDataController(SharedDataService sharedDataService) {
        this.sharedDataService = sharedDataService;
    }

    @GetMapping("/reference-data")
    public ResponseEntity<ReferenceDataResponse> getReferenceData() {
        return ResponseEntity.ok(sharedDataService.getReferenceData());
    }

    @GetMapping("/sections")
    public ResponseEntity<List<SectionResponse>> getSections() {
        return ResponseEntity.ok(sharedDataService.getSections());
    }

    @GetMapping("/teams")
    public ResponseEntity<List<TeamResponse>> getTeams() {
        return ResponseEntity.ok(sharedDataService.getTeams());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(required = false) UserRole role,
                                                       @RequestParam(required = false) Long teamId,
                                                       @RequestParam(required = false) Long sectionId,
                                                       @RequestParam(required = false) String search) {
        return ResponseEntity.ok(sharedDataService.getUsers(role, teamId, sectionId, search));
    }

    @GetMapping("/rubrics")
    public ResponseEntity<List<RubricResponse>> getRubrics() {
        return ResponseEntity.ok(sharedDataService.getRubrics());
    }

    @GetMapping("/active-weeks")
    public ResponseEntity<List<ActiveWeekResponse>> getActiveWeeks() {
        return ResponseEntity.ok(sharedDataService.getActiveWeeks());
    }
}
