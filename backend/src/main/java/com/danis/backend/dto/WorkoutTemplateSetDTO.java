package com.danis.backend.dto;

import lombok.Data;

@Data
public class WorkoutTemplateSetDTO {

    private Integer reps;
    private Double weight;
    private Integer durationSeconds;
    private Double distance;
}
