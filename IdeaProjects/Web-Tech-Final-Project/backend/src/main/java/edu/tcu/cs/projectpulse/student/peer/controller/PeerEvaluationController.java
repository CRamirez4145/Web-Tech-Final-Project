package edu.tcu.cs.projectpulse.student.peer.controller;

import edu.tcu.cs.projectpulse.student.peer.dto.CreatePeerEvaluationRequest;
import edu.tcu.cs.projectpulse.student.peer.dto.PeerEvaluationResponse;
import edu.tcu.cs.projectpulse.student.peer.dto.ReceivedPeerEvaluationSummaryResponse;
import edu.tcu.cs.projectpulse.student.peer.service.PeerEvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/peer-evaluations")
public class PeerEvaluationController {

    private final PeerEvaluationService peerEvaluationService;

    public PeerEvaluationController(PeerEvaluationService peerEvaluationService) {
        this.peerEvaluationService = peerEvaluationService;
    }

    @PostMapping
    public ResponseEntity<PeerEvaluationResponse> createPeerEvaluation(@RequestHeader("X-User-Id") Long studentId,
                                                                       @Valid @RequestBody CreatePeerEvaluationRequest request) {
        PeerEvaluationResponse response = peerEvaluationService.createPeerEvaluation(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/submitted")
    public ResponseEntity<List<PeerEvaluationResponse>> getSubmittedEvaluations(@RequestHeader("X-User-Id") Long studentId) {
        return ResponseEntity.ok(peerEvaluationService.getSubmittedEvaluations(studentId));
    }

    @GetMapping("/received")
    public ResponseEntity<List<ReceivedPeerEvaluationSummaryResponse>> getReceivedEvaluations(@RequestHeader("X-User-Id") Long studentId) {
        return ResponseEntity.ok(peerEvaluationService.getReceivedEvaluations(studentId));
    }
}
