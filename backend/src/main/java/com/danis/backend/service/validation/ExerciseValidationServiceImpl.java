package com.danis.backend.service.validation;

import com.danis.backend.domain.model.entities.Exercise;
import com.danis.backend.exception.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class ExerciseValidationServiceImpl implements ExerciseValidationService{


    @Override
    public void validateExercise(Exercise exercise) {
        if (exercise.getName() == null || exercise.getName().isEmpty()) {
            throw new ValidationException("Exercise name is required");
        }
    }
}
