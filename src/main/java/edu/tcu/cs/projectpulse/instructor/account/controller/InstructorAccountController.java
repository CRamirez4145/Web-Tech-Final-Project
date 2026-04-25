package edu.tcu.cs.projectpulse.instructor.account.controller;

import edu.tcu.cs.projectpulse.instructor.account.dto.InstructorAccountRequest;
import edu.tcu.cs.projectpulse.instructor.account.service.InstructorAccountService;
import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instructor/account")
public class InstructorAccountController {

    private final InstructorAccountService instructorAccountService;

    public InstructorAccountController(InstructorAccountService instructorAccountService) {
        this.instructorAccountService = instructorAccountService;
    }

    @PostMapping
    public ResponseEntity<ManagedUserResponse> createAccount(@Valid @RequestBody InstructorAccountRequest request) {
        ManagedUserResponse response = instructorAccountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
