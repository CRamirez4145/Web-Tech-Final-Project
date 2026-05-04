package edu.tcu.cs.projectpulse.shared.repository;

import edu.tcu.cs.projectpulse.shared.entity.ActiveWeek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActiveWeekRepository extends JpaRepository<ActiveWeek, Long> {

    Optional<ActiveWeek> findByActiveTrue();
}
