package edu.tcu.cs.projectpulse.admin.section.service;

import edu.tcu.cs.projectpulse.admin.section.dto.CreateSectionRequest;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionDetailResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionStudentResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionSummaryResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionTeamResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SectionWeekResponse;
import edu.tcu.cs.projectpulse.admin.section.dto.SetSectionWeeksRequest;
import edu.tcu.cs.projectpulse.shared.entity.ActiveWeek;
import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.shared.entity.Rubric;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.repository.ActiveWeekRepository;
import edu.tcu.cs.projectpulse.shared.repository.RubricRepository;
import edu.tcu.cs.projectpulse.shared.repository.SectionRepository;
import edu.tcu.cs.projectpulse.shared.repository.TeamRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminSectionService {

    private final SectionRepository sectionRepository;
    private final ActiveWeekRepository activeWeekRepository;
    private final RubricRepository rubricRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public AdminSectionService(SectionRepository sectionRepository,
                               ActiveWeekRepository activeWeekRepository,
                               RubricRepository rubricRepository,
                               TeamRepository teamRepository,
                               UserRepository userRepository) {
        this.sectionRepository = sectionRepository;
        this.activeWeekRepository = activeWeekRepository;
        this.rubricRepository = rubricRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SectionDetailResponse createSection(CreateSectionRequest request) {
        Section section = new Section();
        applySectionUpdates(section, request, true);

        return toDetailResponse(sectionRepository.save(section), List.of(), List.of());
    }

    @Transactional
    public SectionDetailResponse updateSection(Long sectionId, CreateSectionRequest request) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        applySectionUpdates(section, request, false);

        return toDetailResponse(
                sectionRepository.save(section),
                teamRepository.findBySectionIdOrderByNameAsc(sectionId),
                userRepository.findBySectionIdOrderByLastNameAscFirstNameAsc(sectionId)
        );
    }

    @Transactional
    public SectionDetailResponse setSectionWeeks(Long sectionId, SetSectionWeeksRequest request) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        List<Long> requestedWeekIds = request.getWeekIds();
        validateNoDuplicateWeekIds(requestedWeekIds);

        List<ActiveWeek> weeks = activeWeekRepository.findAllById(requestedWeekIds);
        if (weeks.size() != requestedWeekIds.size()) {
            throw new ResourceNotFoundException("One or more active weeks were not found.");
        }

        for (ActiveWeek week : weeks) {
            if (week.isActive()) {
                throw new BusinessRuleException("Only inactive weeks can be selected for a section.");
            }
        }

        section.getSelectedWeeks().clear();
        section.getSelectedWeeks().addAll(weeks);

        return toDetailResponse(
                sectionRepository.save(section),
                teamRepository.findBySectionIdOrderByNameAsc(sectionId),
                userRepository.findBySectionIdOrderByLastNameAscFirstNameAsc(sectionId)
        );
    }

    @Transactional(readOnly = true)
    public List<SectionSummaryResponse> findSections(String name) {
        List<Section> sections = hasText(name)
                ? sectionRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name.trim())
                : sectionRepository.findAllByOrderByNameAsc();

        return sections.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SectionDetailResponse getSection(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        return toDetailResponse(
                section,
                teamRepository.findBySectionIdOrderByNameAsc(sectionId),
                userRepository.findBySectionIdOrderByLastNameAscFirstNameAsc(sectionId)
        );
    }

    private SectionSummaryResponse toResponse(Section section) {
        SectionSummaryResponse response = new SectionSummaryResponse();
        response.setId(section.getId());
        response.setName(section.getName());
        return response;
    }

    private SectionTeamResponse toTeamResponse(Team team) {
        SectionTeamResponse response = new SectionTeamResponse();
        response.setId(team.getId());
        response.setName(team.getName());
        return response;
    }

    private SectionStudentResponse toStudentResponse(User user) {
        SectionStudentResponse response = new SectionStudentResponse();
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

    private SectionWeekResponse toWeekResponse(ActiveWeek week) {
        SectionWeekResponse response = new SectionWeekResponse();
        response.setId(week.getId());
        response.setWeekNumber(week.getWeekNumber());
        response.setStartDate(week.getStartDate());
        response.setEndDate(week.getEndDate());
        return response;
    }

    private void applySectionUpdates(Section section, CreateSectionRequest request, boolean creating) {
        String normalizedName = request.getName().trim();

        boolean duplicateName = creating
                ? sectionRepository.existsByNameIgnoreCase(normalizedName)
                : sectionRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, section.getId());

        if (duplicateName) {
            throw new BusinessRuleException("Section name is already in use.");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessRuleException("Section end date must be on or after the start date.");
        }

        Rubric rubric = rubricRepository.findById(request.getRubricId())
                .orElseThrow(() -> new ResourceNotFoundException("Rubric not found with id: " + request.getRubricId()));

        section.setName(normalizedName);
        section.setStartDate(request.getStartDate());
        section.setEndDate(request.getEndDate());
        section.setRubric(rubric);
    }

    private SectionDetailResponse toDetailResponse(Section section, List<Team> teams, List<User> students) {
        SectionDetailResponse response = new SectionDetailResponse();
        response.setId(section.getId());
        response.setName(section.getName());
        response.setStartDate(section.getStartDate());
        response.setEndDate(section.getEndDate());
        if (section.getRubric() != null) {
            response.setRubricId(section.getRubric().getId());
            response.setRubricName(section.getRubric().getName());
        }
        response.setSelectedWeeks(section.getSelectedWeeks().stream()
                .sorted((left, right) -> Integer.compare(left.getWeekNumber(), right.getWeekNumber()))
                .map(this::toWeekResponse)
                .toList());
        response.setTeams(teams.stream().map(this::toTeamResponse).toList());
        response.setStudents(students.stream().map(this::toStudentResponse).toList());
        return response;
    }

    private void validateNoDuplicateWeekIds(List<Long> weekIds) {
        long uniqueCount = weekIds.stream().distinct().count();
        if (uniqueCount != weekIds.size()) {
            throw new BusinessRuleException("Week ids must be unique.");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
