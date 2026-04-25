package edu.tcu.cs.projectpulse.instructor.management.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.instructor.management.dto.AssignInstructorTeamRequest;
import edu.tcu.cs.projectpulse.instructor.management.dto.InstructorInvitationRequest;
import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import edu.tcu.cs.projectpulse.shared.repository.SectionRepository;
import edu.tcu.cs.projectpulse.shared.repository.TeamRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class InstructorManagementService {

    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;
    private final TeamRepository teamRepository;
    private final UserManagementMapper userManagementMapper;

    public InstructorManagementService(UserRepository userRepository,
                                       SectionRepository sectionRepository,
                                       TeamRepository teamRepository,
                                       UserManagementMapper userManagementMapper) {
        this.userRepository = userRepository;
        this.sectionRepository = sectionRepository;
        this.teamRepository = teamRepository;
        this.userManagementMapper = userManagementMapper;
    }

    @Transactional
    public ManagedUserResponse inviteInstructor(InstructorInvitationRequest request) {
        validateEmailIsAvailable(request.getEmail());

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));

        Team team = getOptionalTeam(request.getTeamId());
        validateTeamBelongsToSection(team, section);

        User instructor = new User();
        instructor.setEmail(normalizeEmail(request.getEmail()));
        instructor.setFirstName("Pending");
        instructor.setLastName("Instructor");
        instructor.setRole(UserRole.INSTRUCTOR);
        instructor.setActive(false);
        instructor.setSection(section);
        instructor.setTeam(team);

        return userManagementMapper.toResponse(userRepository.save(instructor));
    }

    @Transactional
    public ManagedUserResponse assignInstructor(Long instructorId, AssignInstructorTeamRequest request) {
        User instructor = getInstructorEntity(instructorId);
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + request.getTeamId()));
        validateTeamBelongsToSection(team, instructor.getSection());
        instructor.setTeam(team);
        return userManagementMapper.toResponse(userRepository.save(instructor));
    }

    @Transactional
    public ManagedUserResponse removeInstructor(Long instructorId) {
        User instructor = getInstructorEntity(instructorId);
        instructor.setTeam(null);
        return userManagementMapper.toResponse(userRepository.save(instructor));
    }

    @Transactional(readOnly = true)
    public List<ManagedUserResponse> findInstructors(String search, Long sectionId, Long teamId, Boolean active) {
        return userRepository.searchUsers(UserRole.INSTRUCTOR, active, sectionId, teamId, normalizeSearch(search))
                .stream()
                .map(userManagementMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ManagedUserResponse getInstructor(Long instructorId) {
        return userManagementMapper.toResponse(getInstructorEntity(instructorId));
    }

    @Transactional
    public ManagedUserResponse deactivateInstructor(Long instructorId) {
        User instructor = getInstructorEntity(instructorId);
        instructor.setActive(false);
        return userManagementMapper.toResponse(userRepository.save(instructor));
    }

    @Transactional
    public ManagedUserResponse reactivateInstructor(Long instructorId) {
        User instructor = getInstructorEntity(instructorId);
        instructor.setActive(true);
        return userManagementMapper.toResponse(userRepository.save(instructor));
    }

    User getInstructorEntity(Long instructorId) {
        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));
        if (user.getRole() != UserRole.INSTRUCTOR) {
            throw new ResourceNotFoundException("Instructor not found with id: " + instructorId);
        }
        return user;
    }

    private void validateEmailIsAvailable(String email) {
        userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .ifPresent(existingUser -> {
                    throw new BusinessRuleException("Email is already in use.");
                });
    }

    private Team getOptionalTeam(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
    }

    private void validateTeamBelongsToSection(Team team, Section section) {
        if (team != null && !team.getSection().getId().equals(section.getId())) {
            throw new BusinessRuleException("The selected team does not belong to the selected section.");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeSearch(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        return "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
    }
}
