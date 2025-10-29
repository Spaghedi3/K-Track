package com.danis.ktrack.service.validation;

import com.danis.ktrack.domain.model.entities.Workout;

import java.util.ArrayList;
import java.util.List;

public class WorkoutValidationServiceImpl implements WorkoutValidationService {

    @Override
    public void validate(Workout workout) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (workout == null) {
            errors.add("Workout cannot be null.");
            throw new ValidationException(errors);
        }

        if (workout.getName() == null || workout.getName().isBlank()) {
            errors.add("Workout name cannot be empty.");
        }

        if (workout.getDate() == null) {
            errors.add("Workout date must be specified.");
        }

        if (workout.getStatus() == null) {
            errors.add("Workout status must be specified.");
        }

        if (workout.getPeriod() == null) {
            errors.add("Workout period cannot be null.");
        }

        if (workout.getUser() == null) {
            errors.add("Workout must be associated with a user.");
        }

        if (workout.getTotalVolume() < 0) {
            errors.add("Total volume cannot be negative.");
        }

        if (workout.getWorkoutExercises() == null) {
            errors.add("Workout exercises list cannot be null (it can be empty).");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}