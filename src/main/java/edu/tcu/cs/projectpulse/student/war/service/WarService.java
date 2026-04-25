package edu.tcu.cs.projectpulse.student.war.service;

import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.shared.entity.ActiveWeek;
import edu.tcu.cs.projectpulse.shared.entity.User;
import edu.tcu.cs.projectpulse.shared.repository.ActiveWeekRepository;
import edu.tcu.cs.projectpulse.shared.repository.UserRepository;
import edu.tcu.cs.projectpulse.student.war.dto.CreateWarRequest;
import edu.tcu.cs.projectpulse.student.war.dto.WarActivityRequest;
import edu.tcu.cs.projectpulse.student.war.dto.WarActivityResponse;
import edu.tcu.cs.projectpulse.student.war.dto.WarResponse;
import edu.tcu.cs.projectpulse.student.war.entity.WarActivity;
import edu.tcu.cs.projectpulse.student.war.entity.WarStatus;
import edu.tcu.cs.projectpulse.student.war.entity.WeeklyActivityReport;
import edu.tcu.cs.projectpulse.student.war.repository.WeeklyActivityReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WarService {

    private final WeeklyActivityReportRepository weeklyActivityReportRepository;
    private final UserRepository userRepository;
    private final ActiveWeekRepository activeWeekRepository;

    public WarService(WeeklyActivityReportRepository weeklyActivityReportRepository,
                      UserRepository userRepository,
                      ActiveWeekRepository activeWeekRepository) {
        this.weeklyActivityReportRepository = weeklyActivityReportRepository;
        this.userRepository = userRepository;
        this.activeWeekRepository = activeWeekRepository;
    }

    @Transactional
    public WarResponse createWar(Long studentId, CreateWarRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        ActiveWeek activeWeek = activeWeekRepository.findById(request.getActiveWeekId())
                .orElseThrow(() -> new ResourceNotFoundException("Active week not found with id: " + request.getActiveWeekId()));

        if (!activeWeek.isActive()) {
            throw new BusinessRuleException("WARs can only be submitted for the currently active week.");
        }

        if (weeklyActivityReportRepository.existsByStudentIdAndActiveWeekId(studentId, activeWeek.getId())) {
            throw new BusinessRuleException("A WAR has already been submitted for this student and week.");
        }

        WeeklyActivityReport report = new WeeklyActivityReport();
        report.setStudent(student);
        report.setTeam(student.getTeam());
        report.setSection(student.getSection());
        report.setActiveWeek(activeWeek);
        report.setSubmittedAt(LocalDateTime.now());
        report.setStatus(WarStatus.SUBMITTED);

        for (WarActivityRequest activityRequest : request.getActivities()) {
            WarActivity activity = new WarActivity();
            activity.setDescription(activityRequest.getDescription().trim());
            activity.setHoursSpent(activityRequest.getHoursSpent());
            activity.setCategory(normalize(activityRequest.getCategory()));
            report.addActivity(activity);
        }

        return toResponse(weeklyActivityReportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public List<WarResponse> getWarsForStudent(Long studentId) {
        validateStudentExists(studentId);
        return weeklyActivityReportRepository.findByStudentIdOrderBySubmittedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WarResponse getWarForStudent(Long studentId, Long warId) {
        validateStudentExists(studentId);
        WeeklyActivityReport report = weeklyActivityReportRepository.findByIdAndStudentId(warId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("WAR not found with id: " + warId));
        return toResponse(report);
    }

    private void validateStudentExists(Long studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
    }

    private WarResponse toResponse(WeeklyActivityReport report) {
        WarResponse response = new WarResponse();
        response.setId(report.getId());
        response.setStudentId(report.getStudent().getId());
        response.setStudentName(report.getStudent().getFirstName() + " " + report.getStudent().getLastName());
        response.setTeamId(report.getTeam().getId());
        response.setSectionId(report.getSection().getId());
        response.setActiveWeekId(report.getActiveWeek().getId());
        response.setWeekNumber(report.getActiveWeek().getWeekNumber());
        response.setSubmittedAt(report.getSubmittedAt());
        response.setStatus(report.getStatus().name());
        response.setActivities(report.getActivities().stream().map(this::toActivityResponse).toList());
        return response;
    }

    private WarActivityResponse toActivityResponse(WarActivity activity) {
        WarActivityResponse response = new WarActivityResponse();
        response.setId(activity.getId());
        response.setDescription(activity.getDescription());
        response.setHoursSpent(activity.getHoursSpent());
        response.setCategory(activity.getCategory());
        return response;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
