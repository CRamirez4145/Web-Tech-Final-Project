package edu.tcu.cs.projectpulse.student.war.repository;

import edu.tcu.cs.projectpulse.student.war.entity.WeeklyActivityReport;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeeklyActivityReportRepository extends JpaRepository<WeeklyActivityReport, Long> {

    boolean existsByStudentIdAndActiveWeekId(Long studentId, Long activeWeekId);

    @EntityGraph(attributePaths = {"activeWeek", "activities"})
    List<WeeklyActivityReport> findByStudentIdOrderBySubmittedAtDesc(Long studentId);

    @EntityGraph(attributePaths = {"student", "team", "section", "activeWeek", "activities"})
    Optional<WeeklyActivityReport> findByIdAndStudentId(Long id, Long studentId);
}
