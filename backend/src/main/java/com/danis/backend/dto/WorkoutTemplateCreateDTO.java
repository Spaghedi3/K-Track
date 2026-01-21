package com.danis.backend.dto;


import lombok.Data;

import java.util.List;

@Data
public class WorkoutTemplateCreateDTO {

    private String name;
    private String description;
    private List<WorkoutTemplateExerciseDTO> exercises;
}
