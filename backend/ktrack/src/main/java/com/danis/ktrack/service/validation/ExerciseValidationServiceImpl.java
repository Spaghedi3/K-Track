package com.danis.ktrack.service.validation;

import com.danis.ktrack.domain.model.entities.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseValidationServiceImpl implements ExerciseValidationService{
    @Override
    public void validate(Exercise exercise) throws ValidationException
    {
        List<String> errors = new ArrayList<>();

        if(exercise == null)
        {
            errors.add("Exercise cannot be null.");
            throw new ValidationException(errors);
        }

        if(exercise.getMetadata()==null)
        {
            errors.add("Exercise metadata cannot be null.");
        }
        else {
            if(exercise.getMetadata().getName()==null || exercise.getMetadata().getName().isBlank())
            {
                errors.add("Exercise name cannot be empty.");
            }
        }

        if(exercise.getPrimaryMuscleGroups()==null || exercise.getPrimaryMuscleGroups().isEmpty())
        {
            errors.add("Exercise must have at least one primary muscle group.");
        }
        if (exercise.getSecondaryMuscleGroups() == null) {
            errors.add("Secondary muscle group list cannot be null (it can be empty).");
        }
        if (exercise.getCategory() == null) {
            errors.add("Exercise category must be specified.");
        }
        if (exercise.getType() == null) {
            errors.add("Exercise type must be specified.");
        }
        if (exercise.isCustom() && exercise.getCreatedByUser() == null) {
            errors.add("A custom exercise must have a creator (createdByUser user cannot be null).");
        }
        if (!exercise.isCustom() && exercise.getCreatedByUser() != null) {
            errors.add("A system exercise (isCustom=false) must not have a creator.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

}
