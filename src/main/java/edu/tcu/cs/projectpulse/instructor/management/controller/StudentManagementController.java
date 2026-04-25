package edu.tcu.cs.projectpulse.instructor.management.controller;

import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
import edu.tcu.cs.projectpulse.instructor.management.service.StudentManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/instructor/students")
public class StudentManagementController {

    private final StudentManagementService studentManagementService;

    public StudentManagementController(StudentManagementService studentManagementService) {
        this.studentManagementService = studentManagementService;
    }

    @GetMapping
    public ResponseEntity<List<ManagedUserResponse>> findStudents(@RequestParam(required = false) String search,
                                                                  @RequestParam(required = false) Long sectionId,
                                                                  @RequestParam(required = false) Long teamId,
                                                                  @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(studentManagementService.findStudents(search, sectionId, teamId, active));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<ManagedUserResponse> getStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentManagementService.getStudent(studentId));
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId) {
        studentManagementService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }
}
