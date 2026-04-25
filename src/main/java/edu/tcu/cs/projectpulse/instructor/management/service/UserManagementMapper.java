package edu.tcu.cs.projectpulse.instructor.management.service;

import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
import edu.tcu.cs.projectpulse.shared.entity.Team;
import edu.tcu.cs.projectpulse.shared.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserManagementMapper {

    public ManagedUserResponse toResponse(User user) {
        ManagedUserResponse response = new ManagedUserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRole(user.getRole().name());
        response.setActive(user.isActive());
        response.setSectionId(user.getSection().getId());
        response.setSectionName(user.getSection().getName());

        Team team = user.getTeam();
        if (team != null) {
            response.setTeamId(team.getId());
            response.setTeamName(team.getName());
        }

        return response;
    }
}
