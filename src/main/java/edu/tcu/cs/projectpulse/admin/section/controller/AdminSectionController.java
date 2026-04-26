package edu.tcu.cs.projectpulse.admin.section.controller;

import edu.tcu.cs.projectpulse.admin.section.dto.CreateSectionRequest;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionDetailResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionSummaryResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SetSectionWeeksRequest;
import edu.tcu.cs.projectpulse.admin.section.service.AdminSectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/sections")
public class AdminSectionController {

    private final AdminSectionService adminSectionService;

    public AdminSectionController(AdminSectionService adminSectionService) {
        this.adminSectionService = adminSectionService;
    }

    @PostMapping
    public ResponseEntity<SectionDetailResponse> createSection(@Valid @RequestBody CreateSectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminSectionService.createSection(request));
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionDetailResponse> updateSection(@PathVariable Long sectionId,
                                                               @Valid @RequestBody CreateSectionRequest request) {
        return ResponseEntity.ok(adminSectionService.updateSection(sectionId, request));
    }

    @PutMapping("/{sectionId}/weeks")
    public ResponseEntity<SectionDetailResponse> setSectionWeeks(@PathVariable Long sectionId,
                                                                 @Valid @RequestBody SetSectionWeeksRequest request) {
        return ResponseEntity.ok(adminSectionService.setSectionWeeks(sectionId, request));
    }

    @GetMapping
    public ResponseEntity<List<SectionSummaryResponse>> findSections(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(adminSectionService.findSections(name));
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionDetailResponse> getSection(@PathVariable Long sectionId) {
        return ResponseEntity.ok(adminSectionService.getSection(sectionId));
    }
}
