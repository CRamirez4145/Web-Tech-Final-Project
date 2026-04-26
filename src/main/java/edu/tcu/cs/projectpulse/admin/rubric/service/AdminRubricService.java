package edu.tcu.cs.projectpulse.admin.rubric.service;

import edu.tcu.cs.projectpulse.admin.rubric.dto.CreateRubricRequest;
import edu.tcu.cs.projectpulse.admin.rubric.dto.RubricCriterionRequest;
import edu.tcu.cs.projectpulse.admin.rubric.dto.RubricCriterionResponse;
import edu.tcu.cs.projectpulse.admin.rubric.dto.RubricResponse;
import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.shared.entity.Rubric;
import edu.tcu.cs.projectpulse.shared.entity.RubricCriterion;
import edu.tcu.cs.projectpulse.shared.repository.RubricCriterionRepository;
import edu.tcu.cs.projectpulse.shared.repository.RubricRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AdminRubricService {

    private final RubricRepository rubricRepository;
    private final RubricCriterionRepository rubricCriterionRepository;

    public AdminRubricService(RubricRepository rubricRepository,
                              RubricCriterionRepository rubricCriterionRepository) {
        this.rubricRepository = rubricRepository;
        this.rubricCriterionRepository = rubricCriterionRepository;
    }

    @Transactional
    public RubricResponse createRubric(CreateRubricRequest request) {
        validateUniqueCriterionNames(request.getCriteria());

        Rubric rubric = new Rubric();
        rubric.setName(request.getName().trim());
        Rubric savedRubric = rubricRepository.save(rubric);

        List<RubricCriterion> savedCriteria = rubricCriterionRepository.saveAll(
                request.getCriteria().stream()
                        .map(criterionRequest -> toCriterionEntity(savedRubric, criterionRequest))
                        .toList()
        );

        return toResponse(savedRubric, savedCriteria);
    }

    private void validateUniqueCriterionNames(List<RubricCriterionRequest> criteria) {
        Set<String> normalizedNames = new HashSet<>();
        for (RubricCriterionRequest criterion : criteria) {
            String normalizedName = criterion.getName().trim().toLowerCase(Locale.ROOT);
            if (!normalizedNames.add(normalizedName)) {
                throw new BusinessRuleException("Rubric criterion names must be unique.");
            }
        }
    }

    private RubricCriterion toCriterionEntity(Rubric rubric, RubricCriterionRequest request) {
        RubricCriterion criterion = new RubricCriterion();
        criterion.setRubric(rubric);
        criterion.setName(request.getName().trim());
        criterion.setDescription(normalize(request.getDescription()));
        criterion.setMaxScore(request.getMaxScore());
        return criterion;
    }

    private RubricResponse toResponse(Rubric rubric, List<RubricCriterion> criteria) {
        RubricResponse response = new RubricResponse();
        response.setId(rubric.getId());
        response.setName(rubric.getName());
        response.setCriteria(criteria.stream().map(this::toCriterionResponse).toList());
        return response;
    }

    private RubricCriterionResponse toCriterionResponse(RubricCriterion criterion) {
        RubricCriterionResponse response = new RubricCriterionResponse();
        response.setId(criterion.getId());
        response.setName(criterion.getName());
        response.setDescription(criterion.getDescription());
        response.setMaxScore(criterion.getMaxScore());
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
