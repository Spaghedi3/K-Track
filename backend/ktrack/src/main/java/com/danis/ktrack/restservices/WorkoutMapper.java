package com.danis.ktrack.restservices;
import com.danis.ktrack.domain.model.entities.*;
import com.danis.ktrack.domain.model.enums.MuscleGroup;
import com.danis.ktrack.domain.model.enums.WorkoutStatus;
import com.danis.ktrack.domain.model.valueobject.WorkoutPeriod;
import com.danis.ktrack.dto.workout.WorkoutCreateRequest;
import com.danis.ktrack.dto.workout.WorkoutUpdateRequest;
import com.danis.ktrack.dto.workout.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WorkoutMapper {

    /**
     * Convert WorkoutCreateRequest to Workout entity
     */
    public Workout toEntity(WorkoutCreateRequest request) {
        Workout workout = new Workout();
        workout.setName(request.getName());
        workout.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());
        workout.setStatus(request.getStatus() != null ? request.getStatus() : WorkoutStatus.IN_PROGRESS);
        workout.setNotes(request.getNotes());

        // Set workout period
        WorkoutPeriod period = new WorkoutPeriod();
        period.setStartTime(LocalDateTime.now());
        workout.setPeriod(period);

        workout.setWorkoutExercises(new ArrayList<>());

        return workout;
    }

    /**
     * Convert WorkoutUpdateRequest to Workout entity
     */
    public Workout toEntity(WorkoutUpdateRequest request) {
        Workout workout = new Workout();
        workout.setName(request.getName());
        workout.setDate(request.getDate());
        workout.setStatus(request.getStatus());
        workout.setNotes(request.getNotes());

        // Update workout period if provided
        if (request.getStartTime() != null || request.getEndTime() != null) {
            WorkoutPeriod period = new WorkoutPeriod();
            period.setStartTime(request.getStartTime());
            period.setEndTime(request.getEndTime());
            workout.setPeriod(period);
        }

        return workout;
    }

    /**
     * Convert Workout entity to WorkoutResponse
     */
    public WorkoutResponse toResponse(Workout workout) {
        WorkoutResponse.WorkoutResponseBuilder builder = WorkoutResponse.builder()
                .id(workout.getId())
                .name(workout.getName())
                .date(workout.getDate())
                .status(workout.getStatus())
                .notes(workout.getNotes())
                .totalVolume(workout.getTotalVolume())
                .createdAt(workout.getCreatedAt())
                .lastModifiedAt(workout.getLastModifiedAt())
                .createdBy(workout.getCreatedBy())
                .lastModifiedBy(workout.getLastModifiedBy());

        // Set period information
        if (workout.getPeriod() != null) {
            builder.startTime(workout.getPeriod().getStartTime())
                    .endTime(workout.getPeriod().getEndTime());

            if (workout.getPeriod().getStartTime() != null && workout.getPeriod().getEndTime() != null) {
                Duration duration = Duration.between(
                        workout.getPeriod().getStartTime(),
                        workout.getPeriod().getEndTime()
                );
                builder.durationMinutes(duration.toMinutes());
            }
        }

        // Set user information
        if (workout.getUser() != null) {
            builder.userId(workout.getUser().getId())
                    .username(workout.getUser().getUsername());
        }

        // Set template information
        if (workout.getTemplate() != null) {
            builder.templateId(workout.getTemplate().getId())
                    .templateName(workout.getTemplate().getName());
        }

        // Map exercises
        if (workout.getWorkoutExercises() != null) {
            List<WorkoutExerciseResponse> exerciseResponses = workout.getWorkoutExercises().stream()
                    .map(this::toExerciseResponse)
                    .collect(Collectors.toList());
            builder.exercises(exerciseResponses);
        }

        return builder.build();
    }

    /**
     * Convert WorkoutExercise to WorkoutExerciseResponse
     */
    private WorkoutExerciseResponse toExerciseResponse(WorkoutExercise workoutExercise) {
        WorkoutExerciseResponse.WorkoutExerciseResponseBuilder builder =
                WorkoutExerciseResponse.builder()
                        .id(workoutExercise.getId())
                        .orderIndex(workoutExercise.getOrderIndex())
                        .notes(workoutExercise.getNotes())
                        .personalRecordAchieved(workoutExercise.isPersonalRecordAchieved());

        // Set exercise information
        if (workoutExercise.getExercise() != null) {
            builder.exerciseId(workoutExercise.getExercise().getId());
            if (workoutExercise.getExercise().getMetadata() != null) {
                builder.exerciseName(workoutExercise.getExercise().getMetadata().getName());
            }
        }

        // Map sets
        if (workoutExercise.getSets() != null) {
            List<WorkoutSetResponse> setResponses = workoutExercise.getSets().stream()
                    .map(this::toSetResponse)
                    .collect(Collectors.toList());
            builder.sets(setResponses);
        }

        return builder.build();
    }

    /**
     * Convert WorkoutSet to WorkoutSetResponse
     */
    private WorkoutSetResponse toSetResponse(WorkoutSet workoutSet) {
        WorkoutSetResponse.WorkoutSetResponseBuilder builder = WorkoutSetResponse.builder()
                .id(workoutSet.getId())
                .setNumber(workoutSet.getSetNumber())
                .setType(workoutSet.getSetType())
                .isCompleted(workoutSet.isCompleted())
                .completedAt(workoutSet.getCompletedAt());

        if (workoutSet.getSetData() != null) {
            builder.reps(workoutSet.getSetData().getReps())
                    .notes(workoutSet.getSetData().getNotes());

            // Weight
            if (workoutSet.getSetData().getWeight() != null) {
                builder.weight(workoutSet.getSetData().getWeight().getValue())
                        .weightUnit(workoutSet.getSetData().getWeight().getUnit() != null ?
                                workoutSet.getSetData().getWeight().getUnit().name() : null);
            }

            // Distance
            if (workoutSet.getSetData().getDistance() != null) {
                builder.distance(workoutSet.getSetData().getDistance().getValue())
                        .distanceUnit(workoutSet.getSetData().getDistance().getUnit() != null ?
                                workoutSet.getSetData().getDistance().getUnit().name() : null);
            }

            // Duration
            if (workoutSet.getSetData().getDuration() != null) {
                builder.duration(workoutSet.getSetData().getDuration().getValue())
                        .durationUnit(workoutSet.getSetData().getDuration().getUnit() != null ?
                                workoutSet.getSetData().getDuration().getUnit().name() : null);
            }
        }

        return builder.build();
    }

    /**
     * Convert Workout entity to WorkoutSummaryResponse
     */
    public WorkoutSummaryResponse toSummaryResponse(Workout workout) {
        WorkoutSummaryResponse.WorkoutSummaryResponseBuilder builder =
                WorkoutSummaryResponse.builder()
                        .id(workout.getId())
                        .name(workout.getName())
                        .date(workout.getDate())
                        .status(workout.getStatus())
                        .totalVolume(workout.getTotalVolume());

        // Time summary
        if (workout.getPeriod() != null) {
            builder.startTime(workout.getPeriod().getStartTime())
                    .endTime(workout.getPeriod().getEndTime());

            if (workout.getPeriod().getStartTime() != null && workout.getPeriod().getEndTime() != null) {
                Duration duration = Duration.between(
                        workout.getPeriod().getStartTime(),
                        workout.getPeriod().getEndTime()
                );
                builder.durationMinutes(duration.toMinutes());
            }
        }

        // Calculate summary statistics
        if (workout.getWorkoutExercises() != null && !workout.getWorkoutExercises().isEmpty()) {
            int totalSets = 0;
            int totalReps = 0;
            int completedExercises = 0;
            int personalRecords = 0;
            Map<String, Integer> exerciseBreakdown = new HashMap<>();
            Map<String, Integer> muscleGroupsTargeted = new HashMap<>();

            for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
                String exerciseName = exercise.getExercise() != null &&
                        exercise.getExercise().getMetadata() != null ?
                        exercise.getExercise().getMetadata().getName() : "Unknown";

                if (exercise.getSets() != null) {
                    int exerciseSets = (int) exercise.getSets().stream()
                            .filter(WorkoutSet::isCompleted)
                            .count();

                    totalSets += exerciseSets;
                    exerciseBreakdown.put(exerciseName, exerciseSets);

                    if (exerciseSets > 0) {
                        completedExercises++;
                    }

                    // Count total reps
                    totalReps += exercise.getSets().stream()
                            .filter(WorkoutSet::isCompleted)
                            .filter(s -> s.getSetData() != null && s.getSetData().getReps() != null)
                            .mapToInt(s -> s.getSetData().getReps())
                            .sum();
                }

                if (exercise.isPersonalRecordAchieved()) {
                    personalRecords++;
                }

                // Track muscle groups
                if (exercise.getExercise() != null &&
                        exercise.getExercise().getPrimaryMuscleGroups() != null) {
                    for (MuscleGroup mg : exercise.getExercise().getPrimaryMuscleGroups()) {
                        muscleGroupsTargeted.merge(mg.name(), 1, Integer::sum);
                    }
                }
            }

            builder.totalExercises(workout.getWorkoutExercises().size())
                    .completedExercises(completedExercises)
                    .totalSets(totalSets)
                    .totalReps(totalReps)
                    .personalRecordsAchieved(personalRecords)
                    .exerciseBreakdown(exerciseBreakdown)
                    .muscleGroupsTargeted(muscleGroupsTargeted);
        } else {
            builder.totalExercises(0)
                    .completedExercises(0)
                    .totalSets(0)
                    .totalReps(0)
                    .personalRecordsAchieved(0)
                    .exerciseBreakdown(new HashMap<>())
                    .muscleGroupsTargeted(new HashMap<>());
        }

        return builder.build();
    }
}