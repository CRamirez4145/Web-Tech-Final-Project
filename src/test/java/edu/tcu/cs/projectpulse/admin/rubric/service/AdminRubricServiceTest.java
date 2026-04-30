package edu.tcu.cs.projectpulse.admin.rubric.service;

import edu.tcu.cs.projectpulse.admin.rubric.dto.CreateRubricRequest;
import edu.tcu.cs.projectpulse.admin.rubric.dto.RubricCriterionRequest;
import edu.tcu.cs.projectpulse.admin.rubric.dto.RubricResponse;
import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.shared.entity.Rubric;
import edu.tcu.cs.projectpulse.shared.entity.RubricCriterion;
import edu.tcu.cs.projectpulse.shared.repository.RubricCriterionRepository;
import edu.tcu.cs.projectpulse.shared.repository.RubricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminRubricServiceTest {

    @Mock
    private RubricRepository rubricRepository;

    @Mock
    private RubricCriterionRepository rubricCriterionRepository;

    @InjectMocks
    private AdminRubricService adminRubricService;

    private CreateRubricRequest request;

    @BeforeEach
    void setUp() {
        RubricCriterionRequest contribution = new RubricCriterionRequest();
        contribution.setName(" Contribution ");
        contribution.setDescription(" Participates consistently ");
        contribution.setMaxScore(5);

        RubricCriterionRequest communication = new RubricCriterionRequest();
        communication.setName("Communication");
        communication.setDescription("   ");
        communication.setMaxScore(10);

        request = new CreateRubricRequest();
        request.setName(" Teamwork Rubric ");
        request.setCriteria(List.of(contribution, communication));
    }

    @Test
    @DisplayName("createRubric saves the rubric and its criteria")
    void createRubricShouldSaveRubricAndCriteria() {
        when(rubricRepository.save(any(Rubric.class))).thenAnswer(invocation -> {
            Rubric rubric = invocation.getArgument(0);
            rubric.setId(1L);
            return rubric;
        });

        when(rubricCriterionRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<RubricCriterion> criteria = invocation.getArgument(0);
            criteria.get(0).setId(11L);
            criteria.get(1).setId(12L);
            return criteria;
        });

        RubricResponse response = adminRubricService.createRubric(request);

        assertEquals(1L, response.getId());
        assertEquals("Teamwork Rubric", response.getName());
        assertEquals(2, response.getCriteria().size());
        assertEquals("Contribution", response.getCriteria().get(0).getName());
        assertEquals(5, response.getCriteria().get(0).getMaxScore());
        assertNull(response.getCriteria().get(1).getDescription());
    }

    @Test
    @DisplayName("createRubric rejects duplicate criterion names")
    void createRubricShouldRejectDuplicateCriterionNames() {
        RubricCriterionRequest duplicate = new RubricCriterionRequest();
        duplicate.setName(" contribution ");
        duplicate.setDescription("Duplicate name");
        duplicate.setMaxScore(3);
        request.setCriteria(List.of(request.getCriteria().get(0), duplicate));

        assertThrows(BusinessRuleException.class, () -> adminRubricService.createRubric(request));
    }
}
