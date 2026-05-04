package edu.tcu.cs.projectpulse.shared.service;

import edu.tcu.cs.projectpulse.shared.dto.ActiveWeekResponse;
import edu.tcu.cs.projectpulse.shared.dto.RubricCriterionResponse;
import edu.tcu.cs.projectpulse.shared.dto.RubricResponse;
import edu.tcu.cs.projectpulse.shared.dto.SectionResponse;
import edu.tcu.cs.projectpulse.shared.dto.TeamResponse;
import edu.tcu.cs.projectpulse.shared.dto.UserResponse;
import edu.tcu.cs.projectpulse.shared.entity.ActiveWeek;
import edu.tcu.cs.projectpulse.shared.entity.Rubric;
import edu.tcu.cs.projectpulse.shared.entity.RubricCriterion;
import edu.tcu.cs.projectpulse.shared.entity.Section;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.repository.RubricCriterionRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResponseMapper {

    private final RubricCriterionRepository rubricCriterionRepository;
    private final UserRepository userRepository;

    public ResponseMapper(RubricCriterionRepository rubricCriterionRepository, UserRepository userRepository) {
        this.rubricCriterionRepository = rubricCriterionRepository;
        this.userRepository = userRepository;
    }

    public SectionResponse toSectionResponse(Section section) {
        SectionResponse response = new SectionResponse();
        response.setId(section.getId());
        response.setName(section.getName());
        return response;
    }

    public ActiveWeekResponse toActiveWeekResponse(ActiveWeek activeWeek) {
        ActiveWeekResponse response = new ActiveWeekResponse();
        response.setId(activeWeek.getId());
        response.setWeekNumber(activeWeek.getWeekNumber());
        response.setStartDate(activeWeek.getStartDate());
        response.setEndDate(activeWeek.getEndDate());
        response.setActive(activeWeek.isActive());
        return response;
    }

    public RubricResponse toRubricResponse(Rubric rubric) {
        RubricResponse response = new RubricResponse();
        response.setId(rubric.getId());
        response.setName(rubric.getName());
        response.setCriteria(rubricCriterionRepository.findByRubricId(rubric.getId()).stream()
                .map(this::toRubricCriterionResponse)
                .toList());
        return response;
    }

    public RubricCriterionResponse toRubricCriterionResponse(RubricCriterion criterion) {
        RubricCriterionResponse response = new RubricCriterionResponse();
        response.setId(criterion.getId());
        response.setName(criterion.getName());
        response.setDescription(criterion.getDescription());
        return response;
    }

    public TeamResponse toTeamResponse(Team team) {
        TeamResponse response = new TeamResponse();
        response.setId(team.getId());
        response.setName(team.getName());
        response.setSectionId(team.getSection().getId());
        response.setSectionName(team.getSection().getName());
        if (team.getInstructor() != null) {
            response.setInstructorId(team.getInstructor().getId());
            response.setInstructorName(team.getInstructor().getFirstName() + " " + team.getInstructor().getLastName());
        }
        response.setStudentCount(userRepository.findByTeamId(team.getId()).size());
        return response;
    }

    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFirstName() + " " + user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        if (user.getTeam() != null) {
            response.setTeamId(user.getTeam().getId());
            response.setTeamName(user.getTeam().getName());
        }
        if (user.getSection() != null) {
            response.setSectionId(user.getSection().getId());
            response.setSectionName(user.getSection().getName());
        }
        return response;
    }

    public List<UserResponse> toUserResponses(List<User> users) {
        return users.stream().map(this::toUserResponse).toList();
    }
}
