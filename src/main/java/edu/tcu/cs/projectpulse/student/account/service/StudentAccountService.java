package edu.tcu.cs.projectpulse.student.account.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.repository.SectionRepository;
import edu.tcu.cs.projectpulse.shared.repository.TeamRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.student.account.dto.StudentAccountRequest;
import edu.tcu.cs.projectpulse.student.account.dto.StudentAccountResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentAccountService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final SectionRepository sectionRepository;

    public StudentAccountService(UserRepository userRepository,
                                 TeamRepository teamRepository,
                                 SectionRepository sectionRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public StudentAccountResponse createAccount(StudentAccountRequest request) {
        validateEmailIsAvailable(request.getEmail(), null);

        Section section = getSection(request.getSectionId());
        Team team = getTeam(request.getTeamId());
        validateTeamBelongsToSection(team, section);

        User user = new User();
        applyAccountUpdates(user, request, team, section);
        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public StudentAccountResponse getAccount(Long studentId) {
        User user = getUser(studentId);
        return toResponse(user);
    }

    @Transactional
    public StudentAccountResponse updateAccount(Long studentId, StudentAccountRequest request) {
        User user = getUser(studentId);
        validateEmailIsAvailable(request.getEmail(), user.getId());

        Section section = getSection(request.getSectionId());
        Team team = getTeam(request.getTeamId());
        validateTeamBelongsToSection(team, section);

        applyAccountUpdates(user, request, team, section);
        return toResponse(userRepository.save(user));
    }

    private User getUser(Long studentId) {
        return userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
    }

    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
    }

    private Section getSection(Long sectionId) {
        return sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
    }

    private void validateEmailIsAvailable(String email, Long currentUserId) {
        userRepository.findByEmailIgnoreCase(email.trim())
                .ifPresent(existingUser -> {
                    if (currentUserId == null || !existingUser.getId().equals(currentUserId)) {
                        throw new BusinessRuleException("Email is already in use.");
                    }
                });
    }

    private void validateTeamBelongsToSection(Team team, Section section) {
        if (!team.getSection().getId().equals(section.getId())) {
            throw new BusinessRuleException("The selected team does not belong to the selected section.");
        }
    }

    private void applyAccountUpdates(User user, StudentAccountRequest request, Team team, Section section) {
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setTeam(team);
        user.setSection(section);
    }

    private StudentAccountResponse toResponse(User user) {
        StudentAccountResponse response = new StudentAccountResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setTeamId(user.getTeam().getId());
        response.setTeamName(user.getTeam().getName());
        response.setSectionId(user.getSection().getId());
        response.setSectionName(user.getSection().getName());
        return response;
    }
}
