package edu.tcu.cs.projectpulse.student.peer.repository;

import edu.tcu.cs.projectpulse.student.peer.entity.PeerEvaluation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PeerEvaluationRepository extends JpaRepository<PeerEvaluation, Long> {

    boolean existsByEvaluatorIdAndEvaluateeIdAndActiveWeekId(Long evaluatorId, Long evaluateeId, Long activeWeekId);

    @EntityGraph(attributePaths = {"evaluatee", "activeWeek", "criterionScores", "criterionScores.criterion"})
    List<PeerEvaluation> findByEvaluatorIdOrderBySubmittedAtDesc(Long evaluatorId);

    @EntityGraph(attributePaths = {"evaluator", "activeWeek", "criterionScores", "criterionScores.criterion"})
    List<PeerEvaluation> findByEvaluateeIdOrderBySubmittedAtDesc(Long evaluateeId);

    long countByEvaluatorIdOrEvaluateeId(Long evaluatorId, Long evaluateeId);

    @EntityGraph(attributePaths = {"evaluator", "evaluatee", "team", "section", "activeWeek", "rubric", "criterionScores", "criterionScores.criterion"})
    @Query("""
            select evaluation
            from PeerEvaluation evaluation
            where (:sectionId is null or evaluation.section.id = :sectionId)
              and (:teamId is null or evaluation.team.id = :teamId)
              and (:evaluateeId is null or evaluation.evaluatee.id = :evaluateeId)
              and (:evaluatorId is null or evaluation.evaluator.id = :evaluatorId)
              and (:activeWeekId is null or evaluation.activeWeek.id = :activeWeekId)
            order by evaluation.activeWeek.weekNumber desc, evaluation.submittedAt desc
            """)
    List<PeerEvaluation> findAllByFilters(@Param("sectionId") Long sectionId,
                                          @Param("teamId") Long teamId,
                                          @Param("evaluateeId") Long evaluateeId,
                                          @Param("evaluatorId") Long evaluatorId,
                                          @Param("activeWeekId") Long activeWeekId);
}
