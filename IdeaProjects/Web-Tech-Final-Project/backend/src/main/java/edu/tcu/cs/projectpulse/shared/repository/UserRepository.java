package edu.tcu.cs.projectpulse.shared.repository;

import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"team", "section"})
    List<User> findAllByOrderByFirstNameAscLastNameAsc();

    @EntityGraph(attributePaths = {"team", "section"})
    List<User> findByTeamId(Long teamId);

    @EntityGraph(attributePaths = {"team", "section"})
    List<User> findBySectionId(Long sectionId);

    @EntityGraph(attributePaths = {"team", "section"})
    List<User> findByRoleOrderByFirstNameAscLastNameAsc(UserRole role);

    @EntityGraph(attributePaths = {"team", "section"})
    List<User> findByRoleAndSectionIdOrderByFirstNameAscLastNameAsc(UserRole role, Long sectionId);

    @EntityGraph(attributePaths = {"team", "section"})
    List<User> findByRoleAndTeamIdOrderByFirstNameAscLastNameAsc(UserRole role, Long teamId);

    @EntityGraph(attributePaths = {"team", "section"})
    List<User> findByRoleAndFirstNameContainingIgnoreCaseOrRoleAndLastNameContainingIgnoreCaseOrderByFirstNameAscLastNameAsc(
            UserRole firstNameRole, String firstName,
            UserRole lastNameRole, String lastName);

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);
}
