package edu.tcu.cs.projectpulse.student.account.controller;

import edu.tcu.cs.projectpulse.student.account.dto.StudentAccountRequest;
import edu.tcu.cs.projectpulse.student.account.dto.StudentAccountResponse;
import edu.tcu.cs.projectpulse.student.account.service.StudentAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/account")
public class StudentAccountController {

    private final StudentAccountService studentAccountService;

    public StudentAccountController(StudentAccountService studentAccountService) {
        this.studentAccountService = studentAccountService;
    }

    @PostMapping
    public ResponseEntity<StudentAccountResponse> createAccount(@Valid @RequestBody StudentAccountRequest request) {
        StudentAccountResponse response = studentAccountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<StudentAccountResponse> getMyAccount(@RequestHeader("X-User-Id") Long studentId) {
        return ResponseEntity.ok(studentAccountService.getAccount(studentId));
    }

    @PutMapping
    public ResponseEntity<StudentAccountResponse> updateMyAccount(@RequestHeader("X-User-Id") Long studentId,
                                                                  @Valid @RequestBody StudentAccountRequest request) {
        return ResponseEntity.ok(studentAccountService.updateAccount(studentId, request));
    }
}
