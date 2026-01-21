package com.danis.backend.dto;

import lombok.Data;

@Data
public class UpdateWorkoutSetRequest {
    private Integer actualReps;
    private Double actualWeight;
    private boolean completed;
}
