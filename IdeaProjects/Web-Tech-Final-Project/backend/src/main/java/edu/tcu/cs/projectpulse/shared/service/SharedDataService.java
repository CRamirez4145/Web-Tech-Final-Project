package edu.tcu.cs.projectpulse.shared.service;

import edu.tcu.cs.projectpulse.shared.dto.ActiveWeekResponse;
import edu.tcu.cs.projectpulse.shared.dto.ReferenceDataResponse;
import edu.tcu.cs.projectpulse.shared.dto.RubricResponse;
import edu.tcu.cs.projectpulse.shared.dto.SectionResponse;
import edu.tcu.cs.projectpulse.shared.dto.TeamResponse;
import edu.tcu.cs.projectpulse.shared.dto.UserResponse;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import edu.tcu.cs.projectpulse.shared.repository.ActiveWeekRepository;
import edu.tcu.cs.projectpulse.shared.repository.RubricRepository;
import edu.tcu.cs.projectpulse.shared.repository.SectionRepository;
import edu.tcu.cs.projectpulse.shared.repository.TeamRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class SharedDataService {

    private final SectionRepository sectionRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final RubricRepository rubricRepository;
    private final ActiveWeekRepository activeWeekRepository;
    private final ResponseMapper responseMapper;

    public SharedDataService(SectionRepository sectionRepository,
                             TeamRepository teamRepository,
                             UserRepository userRepository,
                             RubricRepository rubricRepository,
                             ActiveWeekRepository activeWeekRepository,
                             ResponseMapper responseMapper) {
        this.sectionRepository = sectionRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.rubricRepository = rubricRepository;
        this.activeWeekRepository = activeWeekRepository;
        this.responseMapper = responseMapper;
    }

    @Transactional(readOnly = true)
    public ReferenceDataResponse getReferenceData() {
        ReferenceDataResponse response = new ReferenceDataResponse();
        response.setSections(getSections());
        response.setTeams(getTeams());
        response.setUsers(getUsers(null, null, null, null));
        response.setRubrics(getRubrics());
        response.setActiveWeeks(getActiveWeeks());
        return response;
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> getSections() {
        return sectionRepository.findAll().stream().map(responseMapper::toSectionResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getTeams() {
        return teamRepository.findAll().stream().map(responseMapper::toTeamResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers(UserRole role, Long teamId, Long sectionId, String search) {
        return userRepository.findAllByOrderByFirstNameAscLastNameAsc().stream()
                .filter(user -> role == null || user.getRole() == role)
                .filter(user -> teamId == null || (user.getTeam() != null && user.getTeam().getId().equals(teamId)))
                .filter(user -> sectionId == null || (user.getSection() != null && user.getSection().getId().equals(sectionId)))
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

    @Transactional(readOnly = true)
    public List<RubricResponse> getRubrics() {
        return rubricRepository.findAll().stream()
                .sorted(Comparator.comparing(rubric -> rubric.getId(), Comparator.reverseOrder()))
                .map(responseMapper::toRubricResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActiveWeekResponse> getActiveWeeks() {
        return activeWeekRepository.findAll().stream().map(responseMapper::toActiveWeekResponse).toList();
    }
}
