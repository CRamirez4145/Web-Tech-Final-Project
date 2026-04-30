package edu.tcu.cs.projectpulse.shared.repository;

import edu.tcu.cs.projectpulse.shared.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findAllByOrderByNameAsc();

    List<Section> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
