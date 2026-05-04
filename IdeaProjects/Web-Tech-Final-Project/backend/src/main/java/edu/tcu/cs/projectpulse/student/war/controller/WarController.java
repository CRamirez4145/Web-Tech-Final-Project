package edu.tcu.cs.projectpulse.student.war.controller;

import edu.tcu.cs.projectpulse.student.war.dto.CreateWarRequest;
import edu.tcu.cs.projectpulse.student.war.dto.WarResponse;
import edu.tcu.cs.projectpulse.student.war.service.WarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/wars")
public class WarController {

    private final WarService warService;

    public WarController(WarService warService) {
        this.warService = warService;
    }

    @PostMapping
    public ResponseEntity<WarResponse> createWar(@RequestHeader("X-User-Id") Long studentId,
                                                 @Valid @RequestBody CreateWarRequest request) {
        WarResponse response = warService.createWar(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WarResponse>> getWars(@RequestHeader("X-User-Id") Long studentId) {
        return ResponseEntity.ok(warService.getWarsForStudent(studentId));
    }

    @GetMapping("/{warId}")
    public ResponseEntity<WarResponse> getWar(@RequestHeader("X-User-Id") Long studentId,
                                              @PathVariable Long warId) {
        return ResponseEntity.ok(warService.getWarForStudent(studentId, warId));
    }
}
