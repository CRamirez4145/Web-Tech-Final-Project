package edu.tcu.cs.projectpulse.admin.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.shared.dto.ActiveWeekRequest;
import edu.tcu.cs.projectpulse.shared.dto.ActiveWeekResponse;
import edu.tcu.cs.projectpulse.shared.dto.RubricRequest;
import edu.tcu.cs.projectpulse.shared.dto.RubricResponse;
import edu.tcu.cs.projectpulse.shared.dto.SectionRequest;
import edu.tcu.cs.projectpulse.shared.dto.SectionResponse;
import edu.tcu.cs.projectpulse.shared.dto.TeamRequest;
import edu.tcu.cs.projectpulse.shared.dto.TeamResponse;
import edu.tcu.cs.projectpulse.shared.dto.UserRequest;
import edu.tcu.cs.projectpulse.shared.dto.UserResponse;
import edu.tcu.cs.projectpulse.shared.entity.ActiveWeek;
import edu.tcu.cs.projectpulse.shared.entity.Rubric;
import edu.tcu.cs.projectpulse.shared.entity.RubricCriterion;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import edu.tcu.cs.projectpulse.shared.repository.ActiveWeekRepository;
import edu.tcu.cs.projectpulse.shared.repository.RubricCriterionRepository;
import edu.tcu.cs.projectpulse.shared.repository.RubricRepository;
import edu.tcu.cs.projectpulse.shared.repository.SectionRepository;
import edu.tcu.cs.projectpulse.shared.repository.TeamRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.shared.service.ResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class AdminManagementService {

    private final RubricRepository rubricRepository;
    private final RubricCriterionRepository rubricCriterionRepository;
    private final SectionRepository sectionRepository;
    private final ActiveWeekRepository activeWeekRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ResponseMapper responseMapper;

    public AdminManagementService(RubricRepository rubricRepository,
                                  RubricCriterionRepository rubricCriterionRepository,
                                  SectionRepository sectionRepository,
                                  ActiveWeekRepository activeWeekRepository,
                                  TeamRepository teamRepository,
                                  UserRepository userRepository,
                                  ResponseMapper responseMapper) {
        this.rubricRepository = rubricRepository;
        this.rubricCriterionRepository = rubricCriterionRepository;
        this.sectionRepository = sectionRepository;
        this.activeWeekRepository = activeWeekRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.responseMapper = responseMapper;
    }

    @Transactional
    public RubricResponse createRubric(RubricRequest request) {
        Rubric rubric = new Rubric();
        rubric.setName(request.getName().trim());
        Rubric savedRubric = rubricRepository.save(rubric);

        request.getCriteria().forEach(criterionRequest -> {
            RubricCriterion criterion = new RubricCriterion();
            criterion.setRubric(savedRubric);
            criterion.setName(criterionRequest.getName().trim());
            criterion.setDescription(normalize(criterionRequest.getDescription()));
            rubricCriterionRepository.save(criterion);
        });

        return responseMapper.toRubricResponse(savedRubric);
    }

    @Transactional(readOnly = true)
    public List<RubricResponse> getRubrics() {
        return rubricRepository.findAll().stream()
                .sorted(Comparator.comparing(Rubric::getId).reversed())
                .map(responseMapper::toRubricResponse)
                .toList();
    }

    @Transactional
    public SectionResponse createSection(SectionRequest request) {
        sectionRepository.findByNameIgnoreCase(request.getName().trim())
                .ifPresent(existing -> {
                    throw new BusinessRuleException("A section with that name already exists.");
                });

        Section section = new Section();
        section.setName(request.getName().trim());
        return responseMapper.toSectionResponse(sectionRepository.save(section));
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> getSections() {
        return sectionRepository.findAll().stream().map(responseMapper::toSectionResponse).toList();
    }

    @Transactional
    public SectionResponse updateSection(Long sectionId, SectionRequest request) {
        Section section = getSection(sectionId);
        sectionRepository.findByNameIgnoreCase(request.getName().trim())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(sectionId)) {
                        throw new BusinessRuleException("A section with that name already exists.");
                    }
                });
        section.setName(request.getName().trim());
        return responseMapper.toSectionResponse(sectionRepository.save(section));
    }

    @Transactional
    public ActiveWeekResponse createActiveWeek(ActiveWeekRequest request) {
        ActiveWeek activeWeek = new ActiveWeek();
        applyActiveWeekUpdates(activeWeek, request);
        return responseMapper.toActiveWeekResponse(activeWeekRepository.save(activeWeek));
    }

    @Transactional(readOnly = true)
    public List<ActiveWeekResponse> getActiveWeeks() {
        return activeWeekRepository.findAll().stream().map(responseMapper::toActiveWeekResponse).toList();
    }

    @Transactional
    public ActiveWeekResponse updateActiveWeek(Long activeWeekId, ActiveWeekRequest request) {
        ActiveWeek activeWeek = activeWeekRepository.findById(activeWeekId)
                .orElseThrow(() -> new ResourceNotFoundException("Active week not found with id: " + activeWeekId));
        applyActiveWeekUpdates(activeWeek, request);
        return responseMapper.toActiveWeekResponse(activeWeekRepository.save(activeWeek));
    }

    @Transactional
    public TeamResponse createTeam(TeamRequest request) {
        Team team = new Team();
        applyTeamUpdates(team, request);
        return responseMapper.toTeamResponse(teamRepository.save(team));
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getTeams() {
        return teamRepository.findAll().stream().map(responseMapper::toTeamResponse).toList();
    }

    @Transactional
    public TeamResponse updateTeam(Long teamId, TeamRequest request) {
        Team team = getTeam(teamId);
        applyTeamUpdates(team, request);
        return responseMapper.toTeamResponse(teamRepository.save(team));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers(UserRole role, String search) {
        return userRepository.findAllByOrderByFirstNameAscLastNameAsc().stream()
                .filter(user -> role == null || user.getRole() == role)
                .filter(user -> {
                    if (search == null || search.isBlank()) {
                        return true;
                    }
                    String normalized = search.trim().toLowerCase();
                    return user.getFirstName().toLowerCase().contains(normalized)
                            || user.getLastName().toLowerCase().contains(normalized)
                            || user.getEmail().toLowerCase().contains(normalized);
                })
                .map(responseMapper::toUserResponse)
                .toList();
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        validateEmailAvailable(request.getEmail(), null);
        User user = new User();
        applyUserUpdates(user, request);
        return responseMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserRequest request) {
        User user = getUser(userId);
        validateEmailAvailable(request.getEmail(), userId);
        applyUserUpdates(user, request);
        return responseMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public TeamResponse assignStudentToTeam(Long teamId, Long studentId) {
        Team team = getTeam(teamId);
        User student = getUser(studentId);
        if (student.getRole() != UserRole.STUDENT) {
            throw new BusinessRuleException("Only student users can be assigned as team members.");
        }

        student.setTeam(team);
        student.setSection(team.getSection());
        userRepository.save(student);
        return responseMapper.toTeamResponse(team);
    }

    @Transactional
    public TeamResponse removeStudentFromTeam(Long teamId, Long studentId) {
        Team team = getTeam(teamId);
        User student = getUser(studentId);
        if (student.getTeam() == null || !student.getTeam().getId().equals(teamId)) {
            throw new BusinessRuleException("The selected student is not assigned to this team.");
        }

        student.setTeam(null);
        userRepository.save(student);
        return responseMapper.toTeamResponse(team);
    }

    @Transactional
    public TeamResponse assignInstructorToTeam(Long teamId, Long instructorId) {
        Team team = getTeam(teamId);
        User instructor = getUser(instructorId);
        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new BusinessRuleException("Only instructor users can be assigned as team instructors.");
        }

        instructor.setSection(team.getSection());
        team.setInstructor(instructor);
        userRepository.save(instructor);
        return responseMapper.toTeamResponse(teamRepository.save(team));
    }

    @Transactional
    public TeamResponse removeInstructorFromTeam(Long teamId, Long instructorId) {
        Team team = getTeam(teamId);
        if (team.getInstructor() == null || !team.getInstructor().getId().equals(instructorId)) {
            throw new BusinessRuleException("The selected instructor is not assigned to this team.");
        }

        team.setInstructor(null);
        return responseMapper.toTeamResponse(teamRepository.save(team));
    }

    private void applyActiveWeekUpdates(ActiveWeek activeWeek, ActiveWeekRequest request) {
        if (request.isActive()) {
            activeWeekRepository.findByActiveTrue().ifPresent(current -> {
                if (activeWeek.getId() == null || !current.getId().equals(activeWeek.getId())) {
                    current.setActive(false);
                    activeWeekRepository.save(current);
                }
            });
        }

        activeWeek.setWeekNumber(request.getWeekNumber());
        activeWeek.setStartDate(request.getStartDate());
        activeWeek.setEndDate(request.getEndDate());
        activeWeek.setActive(request.isActive());
    }

    private void applyTeamUpdates(Team team, TeamRequest request) {
        Section section = getSection(request.getSectionId());
        team.setName(request.getName().trim());
        team.setSection(section);

        if (request.getInstructorId() == null) {
            team.setInstructor(null);
            return;
        }

        User instructor = getUser(request.getInstructorId());
        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new BusinessRuleException("Only instructor users can be assigned as team instructors.");
        }
        instructor.setSection(section);
        userRepository.save(instructor);
        team.setInstructor(instructor);
    }

    private void applyUserUpdates(User user, UserRequest request) {
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setRole(request.getRole());

        if (request.getRole() == UserRole.ADMIN) {
            user.setTeam(null);
            user.setSection(null);
            return;
        }

        if (request.getRole() == UserRole.STUDENT) {
            if (request.getTeamId() == null || request.getSectionId() == null) {
                throw new BusinessRuleException("Students must be assigned to both a section and a team.");
            }

            Team team = getTeam(request.getTeamId());
            Section section = getSection(request.getSectionId());
            if (!team.getSection().getId().equals(section.getId())) {
                throw new BusinessRuleException("The selected team does not belong to the selected section.");
            }
            user.setTeam(team);
            user.setSection(section);
            return;
        }

        if (request.getSectionId() == null) {
            throw new BusinessRuleException("Instructors must be assigned to a section.");
        }

        user.setSection(getSection(request.getSectionId()));
        user.setTeam(null);
    }

    private void validateEmailAvailable(String email, Long currentUserId) {
        userRepository.findByEmailIgnoreCase(email.trim())
                .ifPresent(existingUser -> {
                    if (currentUserId == null || !existingUser.getId().equals(currentUserId)) {
                        throw new BusinessRuleException("Email is already in use.");
                    }
                });
    }

    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
    }

    private Section getSection(Long sectionId) {
        return sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
