package edu.tcu.cs.projectpulse.shared.repository;

import edu.tcu.cs.projectpulse.shared.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByOrderByNameAsc();

    List<Team> findBySectionIdOrderByNameAsc(Long sectionId);

    List<Team> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    List<Team> findBySectionIdAndNameContainingIgnoreCaseOrderByNameAsc(Long sectionId, String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
