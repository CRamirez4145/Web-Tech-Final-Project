package edu.tcu.cs.projectpulse.instructor.report.controller;

import edu.tcu.cs.projectpulse.instructor.report.dto.SectionPeerEvaluationReportResponse;
import edu.tcu.cs.projectpulse.instructor.report.dto.StudentPeerEvaluationReportResponse;
import edu.tcu.cs.projectpulse.instructor.report.dto.StudentWarReportResponse;
import edu.tcu.cs.projectpulse.instructor.report.dto.TeamWarReportResponse;
import edu.tcu.cs.projectpulse.instructor.report.service.InstructorReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instructor/reports")
public class InstructorReportController {

    private final InstructorReportService instructorReportService;

    public InstructorReportController(InstructorReportService instructorReportService) {
        this.instructorReportService = instructorReportService;
    }

    @GetMapping("/sections/{sectionId}/peer-evaluations")
    public ResponseEntity<SectionPeerEvaluationReportResponse> getSectionPeerEvaluationReport(@PathVariable Long sectionId,
                                                                                              @RequestParam(required = false) Long activeWeekId) {
        return ResponseEntity.ok(instructorReportService.getSectionPeerEvaluationReport(sectionId, activeWeekId));
    }

    @GetMapping("/teams/{teamId}/wars")
    public ResponseEntity<TeamWarReportResponse> getTeamWarReport(@PathVariable Long teamId,
                                                                  @RequestParam(required = false) Long activeWeekId) {
        return ResponseEntity.ok(instructorReportService.getTeamWarReport(teamId, activeWeekId));
    }

    @GetMapping("/students/{studentId}/peer-evaluations")
    public ResponseEntity<StudentPeerEvaluationReportResponse> getStudentPeerEvaluationReport(@PathVariable Long studentId,
                                                                                              @RequestParam(required = false) Long activeWeekId) {
        return ResponseEntity.ok(instructorReportService.getStudentPeerEvaluationReport(studentId, activeWeekId));
    }

    @GetMapping("/students/{studentId}/wars")
    public ResponseEntity<StudentWarReportResponse> getStudentWarReport(@PathVariable Long studentId,
                                                                        @RequestParam(required = false) Long activeWeekId) {
        return ResponseEntity.ok(instructorReportService.getStudentWarReport(studentId, activeWeekId));
    }
}
