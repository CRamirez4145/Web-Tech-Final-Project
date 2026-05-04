package edu.tcu.cs.projectpulse.shared.dto;

import java.util.List;

public class RubricResponse {

    private Long id;
    private String name;
    private List<RubricCriterionResponse> criteria;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RubricCriterionResponse> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<RubricCriterionResponse> criteria) {
        this.criteria = criteria;
    }
}
