package edu.tcu.cs.projectpulse.student.war.dto;

import java.math.BigDecimal;

public class WarActivityResponse {

    private Long id;
    private String description;
    private BigDecimal hoursSpent;
    private String category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
