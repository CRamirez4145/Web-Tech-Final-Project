package edu.tcu.cs.projectpulse.student.war.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class WarActivityRequest {

    @NotBlank(message = "Description is required.")
    @Size(max = 500, message = "Description must be 500 characters or fewer.")
    private String description;

    @NotNull(message = "Hours spent is required.")
    @DecimalMin(value = "0.00", inclusive = false, message = "Hours spent must be greater than zero.")
    @Digits(integer = 3, fraction = 2, message = "Hours spent must have up to 3 integer digits and 2 decimals.")
    private BigDecimal hoursSpent;

    @Size(max = 100, message = "Category must be 100 characters or fewer.")
    private String category;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getHoursSpent() {
        return hoursSpent;
    }

    public void setHoursSpent(BigDecimal hoursSpent) {
        this.hoursSpent = hoursSpent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
