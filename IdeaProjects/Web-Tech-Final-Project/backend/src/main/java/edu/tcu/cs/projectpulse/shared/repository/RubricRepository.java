package edu.tcu.cs.projectpulse.shared.repository;

import edu.tcu.cs.projectpulse.shared.entity.Rubric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface RubricRepository extends JpaRepository<Rubric, Long> {

    @Override
    @EntityGraph(attributePaths = {})
    List<Rubric> findAll();
}
