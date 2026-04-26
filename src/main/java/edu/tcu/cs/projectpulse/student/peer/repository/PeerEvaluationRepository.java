package edu.tcu.cs.projectpulse.student.peer.repository;

import edu.tcu.cs.projectpulse.student.peer.entity.PeerEvaluation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeerEvaluationRepository extends JpaRepository<PeerEvaluation, Long> {

    boolean existsByEvaluatorIdAndEvaluateeIdAndActiveWeekId(Long evaluatorId, Long evaluateeId, Long activeWeekId);

    boolean existsByTeamId(Long teamId);

    @EntityGraph(attributePaths = {"evaluatee", "activeWeek", "criterionScores", "criterionScores.criterion"})
    List<PeerEvaluation> findByEvaluatorIdOrderBySubmittedAtDesc(Long evaluatorId);

    @EntityGraph(attributePaths = {"evaluator", "activeWeek", "criterionScores", "criterionScores.criterion"})
    List<PeerEvaluation> findByEvaluateeIdOrderBySubmittedAtDesc(Long evaluateeId);
}
