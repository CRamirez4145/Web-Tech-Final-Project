package edu.tcu.cs.projectpulse.admin.team.service;

import edu.tcu.cs.projectpulse.admin.team.dto.AssignStudentsRequest;
import edu.tcu.cs.projectpulse.admin.team.dto.CreateTeamRequest;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamAssignmentResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamAssignmentStudentResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamAssignmentTeamResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamDetailResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamStudentResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamSummaryResponse;
import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.repository.SectionRepository;
import edu.tcu.cs.projectpulse.shared.repository.TeamRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.student.peer.repository.PeerEvaluationRepository;
import edu.tcu.cs.projectpulse.student.war.repository.WeeklyActivityReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminTeamService {

    private final TeamRepository teamRepository;
    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final WeeklyActivityReportRepository weeklyActivityReportRepository;
    private final PeerEvaluationRepository peerEvaluationRepository;

    public AdminTeamService(TeamRepository teamRepository,
                            SectionRepository sectionRepository,
                            UserRepository userRepository,
                            WeeklyActivityReportRepository weeklyActivityReportRepository,
                            PeerEvaluationRepository peerEvaluationRepository) {
        this.teamRepository = teamRepository;
        this.sectionRepository = sectionRepository;
        this.userRepository = userRepository;
        this.weeklyActivityReportRepository = weeklyActivityReportRepository;
        this.peerEvaluationRepository = peerEvaluationRepository;
    }

    @Transactional
    public TeamDetailResponse createTeam(CreateTeamRequest request) {
        Team team = new Team();
        applyTeamUpdates(team, request, true);
        return toDetailResponse(teamRepository.save(team), List.of());
    }

    @Transactional
    public TeamDetailResponse updateTeam(Long teamId, CreateTeamRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        applyTeamUpdates(team, request, false);

        return toDetailResponse(
                teamRepository.save(team),
                userRepository.findByTeamIdOrderByLastNameAscFirstNameAsc(teamId).stream()
                        .map(this::toStudentResponse)
                        .toList()
        );
    }

    @Transactional
    public TeamAssignmentResponse assignStudents(AssignStudentsRequest request) {
        List<Long> studentIds = request.getStudentIds();
        validateNoDuplicateStudentIds(studentIds);

        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + request.getTeamId()));

        List<User> students = userRepository.findAllById(studentIds);
        if (students.size() != studentIds.size()) {
            throw new ResourceNotFoundException("One or more students were not found.");
        }

        for (User student : students) {
            if (!student.getSection().getId().equals(team.getSection().getId())) {
                throw new BusinessRuleException("Students can only be assigned to teams in the same section.");
            }
            student.setTeam(team);
        }

        userRepository.saveAll(students);

        List<Team> sectionTeams = teamRepository.findBySectionIdOrderByNameAsc(team.getSection().getId());
        List<User> sectionStudents = userRepository.findBySectionIdOrderByLastNameAscFirstNameAsc(team.getSection().getId());
        return toAssignmentResponse(team.getSection(), sectionTeams, sectionStudents);
    }

    @Transactional
    public TeamAssignmentResponse removeStudentFromTeam(Long teamId, Long studentId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (student.getTeam() == null || !student.getTeam().getId().equals(teamId)) {
            throw new BusinessRuleException("Student is not assigned to this team.");
        }

        student.setTeam(null);
        userRepository.save(student);

        List<Team> sectionTeams = teamRepository.findBySectionIdOrderByNameAsc(team.getSection().getId());
        List<User> sectionStudents = userRepository.findBySectionIdOrderByLastNameAscFirstNameAsc(team.getSection().getId());
        return toAssignmentResponse(team.getSection(), sectionTeams, sectionStudents);
    }

    @Transactional
    public void deleteTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        if (userRepository.existsByTeamId(teamId)) {
            throw new BusinessRuleException("Team cannot be deleted while students are still assigned. Removing the team would cause data loss.");
        }

        if (weeklyActivityReportRepository.existsByTeamId(teamId)) {
            throw new BusinessRuleException("Team cannot be deleted because weekly activity reports reference it. Deleting the team would cause data loss.");
        }

        if (peerEvaluationRepository.existsByTeamId(teamId)) {
            throw new BusinessRuleException("Team cannot be deleted because peer evaluations reference it. Deleting the team would cause data loss.");
        }

        teamRepository.delete(team);
    }

    @Transactional(readOnly = true)
    public List<TeamSummaryResponse> findTeams(Long sectionId, String name) {
        List<Team> teams;
        boolean hasSection = sectionId != null;
        boolean hasName = hasText(name);

        if (hasSection && hasName) {
            teams = teamRepository.findBySectionIdAndNameContainingIgnoreCaseOrderByNameAsc(sectionId, name.trim());
        } else if (hasSection) {
            teams = teamRepository.findBySectionIdOrderByNameAsc(sectionId);
        } else if (hasName) {
            teams = teamRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name.trim());
        } else {
            teams = teamRepository.findAllByOrderByNameAsc();
        }

        return teams.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TeamDetailResponse getTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        return toDetailResponse(
                team,
                userRepository.findByTeamIdOrderByLastNameAscFirstNameAsc(teamId).stream()
                        .map(this::toStudentResponse)
                        .toList()
        );
    }

    private TeamSummaryResponse toResponse(Team team) {
        TeamSummaryResponse response = new TeamSummaryResponse();
        response.setId(team.getId());
        response.setName(team.getName());
        response.setDescription(team.getDescription());
        response.setWebsite(team.getWebsite());
        response.setSectionId(team.getSection().getId());
        response.setSectionName(team.getSection().getName());
        return response;
    }

    private TeamDetailResponse toDetailResponse(Team team, List<TeamStudentResponse> students) {
        TeamDetailResponse response = new TeamDetailResponse();
        response.setId(team.getId());
        response.setName(team.getName());
        response.setDescription(team.getDescription());
        response.setWebsite(team.getWebsite());
        response.setSectionId(team.getSection().getId());
        response.setSectionName(team.getSection().getName());
        response.setStudents(students);
        return response;
    }

    private TeamStudentResponse toStudentResponse(User user) {
        TeamStudentResponse response = new TeamStudentResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        return response;
    }

    private TeamAssignmentTeamResponse toAssignmentTeamResponse(Team team, int studentCount) {
        TeamAssignmentTeamResponse response = new TeamAssignmentTeamResponse();
        response.setId(team.getId());
        response.setName(team.getName());
        response.setStudentCount(studentCount);
        return response;
    }

    private TeamAssignmentStudentResponse toAssignmentStudentResponse(User user) {
        TeamAssignmentStudentResponse response = new TeamAssignmentStudentResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        if (user.getTeam() != null) {
            response.setTeamId(user.getTeam().getId());
            response.setTeamName(user.getTeam().getName());
        }
        return response;
    }

    private TeamAssignmentResponse toAssignmentResponse(Section section, List<Team> teams, List<User> students) {
        Map<Long, Integer> teamCounts = new LinkedHashMap<>();
        for (Team team : teams) {
            teamCounts.put(team.getId(), 0);
        }
        for (User student : students) {
            if (student.getTeam() != null) {
                teamCounts.computeIfPresent(student.getTeam().getId(), (key, count) -> count + 1);
            }
        }

        TeamAssignmentResponse response = new TeamAssignmentResponse();
        response.setSectionId(section.getId());
        response.setSectionName(section.getName());
        response.setTeams(teams.stream()
                .map(team -> toAssignmentTeamResponse(team, teamCounts.getOrDefault(team.getId(), 0)))
                .toList());
        response.setStudents(students.stream().map(this::toAssignmentStudentResponse).toList());
        return response;
    }

    private void applyTeamUpdates(Team team, CreateTeamRequest request, boolean creating) {
        String normalizedName = request.getName().trim();

        boolean duplicateName = creating
                ? teamRepository.existsByNameIgnoreCase(normalizedName)
                : teamRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, team.getId());

        if (duplicateName) {
            throw new BusinessRuleException("Team name is already in use.");
        }

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));

        team.setName(normalizedName);
        team.setDescription(normalize(request.getDescription()));
        team.setWebsite(normalize(request.getWebsite()));
        team.setSection(section);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validateNoDuplicateStudentIds(List<Long> studentIds) {
        long uniqueCount = studentIds.stream().distinct().count();
        if (uniqueCount != studentIds.size()) {
            throw new BusinessRuleException("Student ids must be unique.");
        }
    }
}
