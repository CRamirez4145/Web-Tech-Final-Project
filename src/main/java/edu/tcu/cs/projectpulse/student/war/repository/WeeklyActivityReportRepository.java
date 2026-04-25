package edu.tcu.cs.projectpulse.student.war.repository;

import edu.tcu.cs.projectpulse.student.war.entity.WeeklyActivityReport;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WeeklyActivityReportRepository extends JpaRepository<WeeklyActivityReport, Long> {

    boolean existsByStudentIdAndActiveWeekId(Long studentId, Long activeWeekId);

    @EntityGraph(attributePaths = {"activeWeek", "activities"})
    List<WeeklyActivityReport> findByStudentIdOrderBySubmittedAtDesc(Long studentId);

    @EntityGraph(attributePaths = {"student", "team", "section", "activeWeek", "activities"})
    Optional<WeeklyActivityReport> findByIdAndStudentId(Long id, Long studentId);

    long countByStudentId(Long studentId);

    @EntityGraph(attributePaths = {"student", "team", "section", "activeWeek", "activities"})
    @Query("""
            select war
            from WeeklyActivityReport war
            where (:sectionId is null or war.section.id = :sectionId)
              and (:teamId is null or war.team.id = :teamId)
              and (:studentId is null or war.student.id = :studentId)
              and (:activeWeekId is null or war.activeWeek.id = :activeWeekId)
            order by war.activeWeek.weekNumber desc, war.submittedAt desc
            """)
    List<WeeklyActivityReport> findAllByFilters(@Param("sectionId") Long sectionId,
                                                @Param("teamId") Long teamId,
                                                @Param("studentId") Long studentId,
                                                @Param("activeWeekId") Long activeWeekId);
}
