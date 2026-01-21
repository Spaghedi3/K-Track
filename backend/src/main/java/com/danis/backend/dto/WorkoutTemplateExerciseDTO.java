package com.danis.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkoutTemplateExerciseDTO {

    private Long exerciseId;
    private int orderIndex;
    private String imageUrl;
    private List<WorkoutTemplateSetDTO> sets;
}
