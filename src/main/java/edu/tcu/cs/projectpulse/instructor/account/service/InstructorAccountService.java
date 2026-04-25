package edu.tcu.cs.projectpulse.instructor.account.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.instructor.account.dto.InstructorAccountRequest;
import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
import edu.tcu.cs.projectpulse.instructor.management.service.UserManagementMapper;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class InstructorAccountService {

    private final UserRepository userRepository;
    private final UserManagementMapper userManagementMapper;

    public InstructorAccountService(UserRepository userRepository,
                                    UserManagementMapper userManagementMapper) {
        this.userRepository = userRepository;
        this.userManagementMapper = userManagementMapper;
    }

    @Transactional
    public ManagedUserResponse createAccount(InstructorAccountRequest request) {
        User instructor = userRepository.findByEmailIgnoreCase(normalizeEmail(request.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("No instructor invitation found for email: " + request.getEmail()));

        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new BusinessRuleException("The provided email does not belong to an instructor invitation.");
        }

        instructor.setFirstName(request.getFirstName().trim());
        instructor.setLastName(request.getLastName().trim());
        instructor.setActive(true);

        return userManagementMapper.toResponse(userRepository.save(instructor));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
