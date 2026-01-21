package com.danis.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutTemplateExerciseUpdateDTO {
    private Long exerciseId;
    private Integer orderIndex;
    private List<WorkoutTemplateSetDTO> sets;
}