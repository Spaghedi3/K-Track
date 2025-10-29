package com.danis.ktrack.service.validation;

import com.danis.ktrack.domain.model.entities.WorkoutTemplate;

import java.util.ArrayList;
import java.util.List;

public class WorkoutTemplateValidationServiceImpl implements WorkoutTemplateValidationService {

    @Override
    public void validate(WorkoutTemplate workoutTemplate) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (workoutTemplate == null) {
            errors.add("WorkoutTemplate cannot be null.");
            throw new ValidationException(errors);
        }


        if (workoutTemplate.getName() == null || workoutTemplate.getName().isBlank()) {
            errors.add("WorkoutTemplate name cannot be empty.");
        }


        if (workoutTemplate.getCreatedByUser() == null) {
            errors.add("WorkoutTemplate must have a creator (createdByUser user cannot be null).");
        }


        if (workoutTemplate.getTemplateExercises() == null || workoutTemplate.getTemplateExercises().isEmpty()) {
            errors.add("WorkoutTemplate must contain at least one exercise.");
        } else {

        }


        if (workoutTemplate.getTags() == null) {
            errors.add("WorkoutTemplate tags list cannot be null (it can be empty).");
        }


        if (workoutTemplate.getCreatedAt() == null) {
            errors.add("WorkoutTemplate creation date must be specified.");
        }


        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
