package edu.tcu.cs.projectpulse.admin.rubric.controller;

import edu.tcu.cs.projectpulse.admin.rubric.dto.CreateRubricRequest;
import edu.tcu.cs.projectpulse.admin.rubric.dto.RubricResponse;
import edu.tcu.cs.projectpulse.admin.rubric.service.AdminRubricService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/rubrics")
public class AdminRubricController {

    private final AdminRubricService adminRubricService;

    public AdminRubricController(AdminRubricService adminRubricService) {
        this.adminRubricService = adminRubricService;
    }

    @PostMapping
    public ResponseEntity<RubricResponse> createRubric(@Valid @RequestBody CreateRubricRequest request) {
        RubricResponse response = adminRubricService.createRubric(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
