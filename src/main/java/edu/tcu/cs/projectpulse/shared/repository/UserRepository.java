package edu.tcu.cs.projectpulse.shared.repository;

import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"team", "section"})
    List<User> findByTeamId(Long teamId);

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"team", "section"})
    @Query("""
            select u
            from User u
            where (:role is null or u.role = :role)
              and (:active is null or u.active = :active)
              and (:sectionId is null or u.section.id = :sectionId)
              and (:teamId is null or (u.team is not null and u.team.id = :teamId))
              and (:searchTerm is null
                   or lower(u.firstName) like :searchTerm
                   or lower(u.lastName) like :searchTerm
                   or lower(u.email) like :searchTerm
                   or lower(concat(u.firstName, ' ', u.lastName)) like :searchTerm
                   or lower(concat(u.lastName, ' ', u.firstName)) like :searchTerm)
            order by u.lastName asc, u.firstName asc
            """)
    List<User> searchUsers(@Param("role") UserRole role,
                           @Param("active") Boolean active,
                           @Param("sectionId") Long sectionId,
                           @Param("teamId") Long teamId,
                           @Param("searchTerm") String searchTerm);
}
