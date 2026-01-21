package com.danis.backend.service.workflow;

import com.danis.backend.domain.model.entities.Workout;
import com.danis.backend.dto.UpdateWorkoutSetRequest;
import com.danis.backend.dto.WorkoutDetailResponse;
import com.danis.backend.dto.WorkoutSetResponse;

import java.util.Optional;

public interface WorkoutWorkflowService {

    Workout startWorkout(Long userId, Long templateId);

    Workout finishWorkout(Long workoutId, Long userId);
    WorkoutDetailResponse getWorkoutDetails(Long workoutId, Long userId);
    Optional<WorkoutDetailResponse> getActiveWorkout(Long userId);
    WorkoutSetResponse updateWorkoutSet(Long workoutId, Long exerciseId, Long setId, Long userId, UpdateWorkoutSetRequest request);
    // WorkoutWorkflowService.java (interface)
    Workout pauseWorkout(Long workoutId, Long userId);
    Workout resumeWorkout(Long workoutId, Long userId);
}
