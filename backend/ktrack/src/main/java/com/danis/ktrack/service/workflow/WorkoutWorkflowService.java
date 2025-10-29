package com.danis.ktrack.service.workflow;

import com.danis.ktrack.domain.model.entities.Workout;
import com.danis.ktrack.service.validation.ValidationException;

public interface WorkoutWorkflowService {

    /**
     * Use Case: Start New Workout
     * @param newWorkout The Workout object with initial details (name, date, user, status).
     * @param userId     The ID of the user starting the workout.
     * @return The persisted Workout entity.
     * @throws ValidationException if the workout data is invalid.
     * @throws RuntimeException    if the user is not found.
     */
    Workout startWorkout(Workout newWorkout, Long userId) throws ValidationException;

    /**
     * Use Case: Update Workout Details
     * Validates and updates an existing workout (e.g., adds notes, changes name, updates status).
     *
     * @param workoutId      The ID of the workout to update.
     * @param workoutDetails Workout object with the new details.
     * @return The updated Workout entity.
     * @throws ValidationException if the new workout data is invalid.
     * @throws RuntimeException    if the workout is not found.
     */
    Workout updateWorkout(Long workoutId, Workout workoutDetails) throws ValidationException;
}