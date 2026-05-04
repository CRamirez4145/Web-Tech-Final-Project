package edu.tcu.cs.projectpulse.instructor.controller;

import edu.tcu.cs.projectpulse.instructor.service.InstructorReportService;
import edu.tcu.cs.projectpulse.student.peer.dto.PeerEvaluationResponse;
import edu.tcu.cs.projectpulse.student.war.dto.WarResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/instructor/reports")
public class InstructorReportController {

    private final InstructorReportService instructorReportService;

    public InstructorReportController(InstructorReportService instructorReportService) {
        this.instructorReportService = instructorReportService;
    }

    @GetMapping("/team-wars")
    public ResponseEntity<List<WarResponse>> getTeamWars(@RequestParam Long teamId) {
        return ResponseEntity.ok(instructorReportService.getTeamWars(teamId));
    }

    @GetMapping("/student-wars")
    public ResponseEntity<List<WarResponse>> getStudentWars(@RequestParam Long studentId) {
        return ResponseEntity.ok(instructorReportService.getStudentWars(studentId));
    }

    @GetMapping("/section-peer-evaluations")
    public ResponseEntity<List<PeerEvaluationResponse>> getSectionPeerEvaluations(@RequestParam Long sectionId) {
        return ResponseEntity.ok(instructorReportService.getSectionPeerEvaluations(sectionId));
    }

    @GetMapping("/student-peer-evaluations")
    public ResponseEntity<List<PeerEvaluationResponse>> getStudentPeerEvaluations(@RequestParam Long studentId) {
        return ResponseEntity.ok(instructorReportService.getStudentPeerEvaluations(studentId));
    }
}
