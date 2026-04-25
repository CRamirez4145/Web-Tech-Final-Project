package edu.tcu.cs.projectpulse.instructor.management.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.entity.UserRole;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.student.peer.repository.PeerEvaluationRepository;
import edu.tcu.cs.projectpulse.student.war.repository.WeeklyActivityReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class StudentManagementService {

    private final UserRepository userRepository;
    private final WeeklyActivityReportRepository weeklyActivityReportRepository;
    private final PeerEvaluationRepository peerEvaluationRepository;
    private final UserManagementMapper userManagementMapper;

    public StudentManagementService(UserRepository userRepository,
                                    WeeklyActivityReportRepository weeklyActivityReportRepository,
                                    PeerEvaluationRepository peerEvaluationRepository,
                                    UserManagementMapper userManagementMapper) {
        this.userRepository = userRepository;
        this.weeklyActivityReportRepository = weeklyActivityReportRepository;
        this.peerEvaluationRepository = peerEvaluationRepository;
        this.userManagementMapper = userManagementMapper;
    }

    @Transactional(readOnly = true)
    public List<ManagedUserResponse> findStudents(String search, Long sectionId, Long teamId, Boolean active) {
        return userRepository.searchUsers(UserRole.STUDENT, active, sectionId, teamId, normalizeSearch(search))
                .stream()
                .map(userManagementMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ManagedUserResponse getStudent(Long studentId) {
        return userManagementMapper.toResponse(getStudentEntity(studentId));
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        User student = getStudentEntity(studentId);
        if (weeklyActivityReportRepository.countByStudentId(studentId) > 0
                || peerEvaluationRepository.countByEvaluatorIdOrEvaluateeId(studentId, studentId) > 0) {
            throw new BusinessRuleException("Students with submitted WARs or peer evaluations cannot be deleted.");
        }
        userRepository.delete(student);
    }

    User getStudentEntity(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        if (user.getRole() != UserRole.STUDENT) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        return user;
    }

    private String normalizeSearch(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        return "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
    }
}
