package com.danis.ktrack.service.validation;

import com.danis.ktrack.domain.model.entities.ExerciseStatistics;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExerciseStatisticsValidationServiceImpl implements ExerciseStatisticsValidationService{

    @Override
    public void validate(ExerciseStatistics stats) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (stats == null) {
            errors.add("ExerciseStatistics cannot be null.");
            throw new ValidationException(errors);
        }

        if (stats.getUser() == null) {
            errors.add("Statistics must be linked to a User.");
        }
        if (stats.getExercise() == null) {
            errors.add("Statistics must be linked to an Exercise.");
        }


        if (stats.getPersonalRecords() == null) {
            errors.add("Personal records list cannot be null (it can be empty).");
        }
        if (stats.getVolumeHistory() == null) {
            errors.add("Volume history list cannot be null (it can be empty).");
        }

        if (stats.getTotalWorkouts() < 0) {
            errors.add("Total workouts cannot be negative.");
        }
        if (stats.getTotalSets() < 0) {
            errors.add("Total sets cannot be negative.");
        }
        if (stats.getTotalReps() < 0) {
            errors.add("Total reps cannot be negative.");
        }
        if (stats.getTotalVolume() < 0) {
            errors.add("Total volume cannot be negative.");
        }
        if (stats.getLastPerformed() != null && stats.getLastPerformed().isAfter(LocalDateTime.now())) {
            errors.add("Last performed date cannot be in the future.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }



}
