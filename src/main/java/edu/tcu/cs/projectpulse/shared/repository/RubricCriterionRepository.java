package edu.tcu.cs.projectpulse.shared.repository;

import edu.tcu.cs.projectpulse.shared.entity.RubricCriterion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RubricCriterionRepository extends JpaRepository<RubricCriterion, Long> {

    List<RubricCriterion> findByRubricId(Long rubricId);
}
