package com.danis.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkoutSetResponse {
    private Long id;
    private Integer setNumber;
    private Integer plannedReps;
    private Double plannedWeight;
    private Integer actualReps;
    private Double actualWeight;
    private boolean completed;
}