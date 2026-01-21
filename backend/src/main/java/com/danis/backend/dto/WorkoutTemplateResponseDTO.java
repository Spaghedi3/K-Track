package com.danis.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkoutTemplateResponseDTO {

    private Long id;
    private String name;
    private String description;
    private List<WorkoutTemplateExerciseResponseDTO> exercises;
}
