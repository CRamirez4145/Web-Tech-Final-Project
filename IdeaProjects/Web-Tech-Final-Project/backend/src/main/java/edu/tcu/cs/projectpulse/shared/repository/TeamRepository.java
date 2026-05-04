package edu.tcu.cs.projectpulse.shared.repository;

import edu.tcu.cs.projectpulse.shared.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Override
    @EntityGraph(attributePaths = {"section", "instructor"})
    List<Team> findAll();

    @EntityGraph(attributePaths = {"section", "instructor"})
    List<Team> findBySectionIdOrderByNameAsc(Long sectionId);
}
