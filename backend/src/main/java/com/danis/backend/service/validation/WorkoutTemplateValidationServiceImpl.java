package com.danis.backend.service.validation;

import com.danis.backend.dto.WorkoutTemplateCreateDTO;
import org.springframework.stereotype.Service;

@Service
public class WorkoutTemplateValidationServiceImpl implements WorkoutTemplateValidationService{
    public void validateCreate(WorkoutTemplateCreateDTO dto) {

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Workout template name is required");
        }

        if (dto.getExercises() == null || dto.getExercises().isEmpty()) {
            throw new IllegalArgumentException("Workout template must have at least one exercise");
        }

        dto.getExercises().forEach(ex -> {
            if (ex.getSets() == null || ex.getSets().isEmpty()) {
                throw new IllegalArgumentException("Each exercise must have at least one set");
            }
        });
    }
}
