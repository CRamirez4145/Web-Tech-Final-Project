package edu.tcu.cs.projectpulse.admin.section.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateSectionRequest {

    @NotBlank(message = "Section name is required.")
    @Size(max = 50, message = "Section name must be 50 characters or fewer.")
    private String name;

    @NotNull(message = "Section start date is required.")
    private LocalDate startDate;

    @NotNull(message = "Section end date is required.")
    private LocalDate endDate;

    @NotNull(message = "Rubric id is required.")
    private Long rubricId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getRubricId() {
        return rubricId;
    }

    public void setRubricId(Long rubricId) {
        this.rubricId = rubricId;
    }
}
