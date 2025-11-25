package com.danis.ktrack.restservices;

import com.danis.ktrack.domain.model.entities.*;
import com.danis.ktrack.domain.repository.ExerciseRepository;
import com.danis.ktrack.domain.repository.WorkoutRepository;
import com.danis.ktrack.dto.workout.WorkoutExerciseCreateDTO;
import com.danis.ktrack.dto.workout.WorkoutExerciseDTO;
import com.danis.ktrack.dto.workout.WorkoutSetCreateDTO;
import com.danis.ktrack.dto.workout.WorkoutSetDTO;
import com.danis.ktrack.domain.model.valueobject.WorkoutSetData;
import com.danis.ktrack.service.computation.StaticticsComputationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/workouts/{workoutId}/exercises")
@RequiredArgsConstructor
public class WorkoutExerciseController {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutMapper workoutMapper;
    private final StaticticsComputationService statisticsComputationService;

    /**
     * POST /api/workouts/{workoutId}/exercises
     */

    @PostMapping
    public ResponseEntity<WorkoutExerciseDTO> addExerciseToWorkout(
            @PathVariable Long workoutId,
            @Valid @RequestBody WorkoutExerciseCreateDTO createDTO) {
        log.info("Adding exercise {} to workout {}", createDTO.getExerciseId(), workoutId);

        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        Exercise exercise = exerciseRepository.findById(createDTO.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + createDTO.getExerciseId()));

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setWorkout(workout);
        workoutExercise.setExercise(exercise);
        workoutExercise.setOrderIndex(createDTO.getOrderIndex());
        workoutExercise.setNotes(createDTO.getNotes());
        workoutExercise.setSets(List.of());

        // Add to workout's exercise list
        if (workout.getWorkoutExercises() == null) {
            workout.setWorkoutExercises(List.of(workoutExercise));
        } else {
            workout.getWorkoutExercises().add(workoutExercise);
        }

        Workout savedWorkout = workoutRepository.save(workout);
        WorkoutExerciseDTO dto = mapToWorkoutExerciseDTO(workoutExercise);

        log.info("Exercise added successfully with ID: {}", workoutExercise.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * PUT /api/workouts/{workoutId}/exercises/{exerciseId}
     * Update exercise in workout
     */
    @PutMapping("/{exerciseId}")
    public ResponseEntity<WorkoutExerciseDTO> updateExerciseInWorkout(
            @PathVariable Long workoutId,
            @PathVariable Long exerciseId,
            @Valid @RequestBody WorkoutExerciseCreateDTO updateDTO) {
        log.info("Updating exercise {} in workout {}", exerciseId, workoutId);

        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        WorkoutExercise workoutExercise = workout.getWorkoutExercises().stream()
                .filter(we -> we.getId().equals(exerciseId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Exercise not found in workout with id: " + exerciseId));

        workoutExercise.setOrderIndex(updateDTO.getOrderIndex());
        workoutExercise.setNotes(updateDTO.getNotes());

        Workout savedWorkout = workoutRepository.save(workout);
        WorkoutExerciseDTO dto = mapToWorkoutExerciseDTO(workoutExercise);

        log.info("Exercise updated successfully");
        return ResponseEntity.ok(dto);
    }

    /**
     * DELETE /api/workouts/{workoutId}/exercises/{exerciseId}
     * Remove exercise from workout
     */
    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<Void> removeExerciseFromWorkout(
            @PathVariable Long workoutId,
            @PathVariable Long exerciseId) {
        log.info("Removing exercise {} from workout {}", exerciseId, workoutId);

        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        workout.getWorkoutExercises().removeIf(we -> we.getId().equals(exerciseId));

        workoutRepository.save(workout);
        log.info("Exercise removed successfully");

        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/workouts/{workoutId}/exercises/{exerciseId}/sets
     * Add a set to exercise
     */
    @PostMapping("/{exerciseId}/sets")
    public ResponseEntity<WorkoutSetDTO> addSetToExercise(
            @PathVariable Long workoutId,
            @PathVariable Long exerciseId,
            @Valid @RequestBody WorkoutSetCreateDTO createDTO) {
        log.info("Adding set to exercise {} in workout {}", exerciseId, workoutId);

        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        WorkoutExercise workoutExercise = workout.getWorkoutExercises().stream()
                .filter(we -> we.getId().equals(exerciseId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Exercise not found in workout with id: " + exerciseId));

        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setWorkoutExercise(workoutExercise);
        workoutSet.setSetNumber(createDTO.getSetNumber());
        workoutSet.setSetType(createDTO.getSetType());


        // Build set data
        WorkoutSetData setData = new WorkoutSetData();
        setData.setReps(createDTO.getReps());
        setData.setWeight(createDTO.getWeight());
        setData.setDistance(createDTO.getDistance());
        setData.setDuration(createDTO.getDuration());
        setData.setRestTime(createDTO.getRestTime());
        setData.setNotes(createDTO.getNotes());
        workoutSet.setSetData(setData);

        if (workoutExercise.getSets() == null) {
            workoutExercise.setSets(List.of(workoutSet));
        } else {
            workoutExercise.getSets().add(workoutSet);
        }

        Workout savedWorkout = workoutRepository.save(workout);
        WorkoutSetDTO dto = mapToWorkoutSetDTO(workoutSet);

        log.info("Set added successfully with ID: {}", workoutSet.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * PUT /api/workouts/{workoutId}/exercises/{exerciseId}/sets/{setId}
     * Update set
     */
    @PutMapping("/{exerciseId}/sets/{setId}")
    public ResponseEntity<WorkoutSetDTO> updateSet(
            @PathVariable Long workoutId,
            @PathVariable Long exerciseId,
            @PathVariable Long setId,
            @Valid @RequestBody WorkoutSetCreateDTO updateDTO) {
        log.info("Updating set {} for exercise {} in workout {}", setId, exerciseId, workoutId);

        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        WorkoutExercise workoutExercise = workout.getWorkoutExercises().stream()
                .filter(we -> we.getId().equals(exerciseId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Exercise not found in workout with id: " + exerciseId));

        WorkoutSet workoutSet = workoutExercise.getSets().stream()
                .filter(s -> s.getId().equals(setId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Set not found with id: " + setId));

        workoutSet.setSetNumber(updateDTO.getSetNumber());
        workoutSet.setSetType(updateDTO.getSetType());

        // Update set data
        if (workoutSet.getSetData() == null) {
            workoutSet.setSetData(new WorkoutSetData());
        }
        WorkoutSetData setData = workoutSet.getSetData();
        setData.setReps(updateDTO.getReps());
        setData.setWeight(updateDTO.getWeight());
        setData.setDistance(updateDTO.getDistance());
        setData.setDuration(updateDTO.getDuration());
        setData.setRestTime(updateDTO.getRestTime());
        setData.setNotes(updateDTO.getNotes());

        Workout savedWorkout = workoutRepository.save(workout);
        WorkoutSetDTO dto = mapToWorkoutSetDTO(workoutSet);

        log.info("Set updated successfully");
        return ResponseEntity.ok(dto);
    }

    /**
     * DELETE /api/workouts/{workoutId}/exercises/{exerciseId}/sets/{setId}
     * Delete set
     */
    @DeleteMapping("/{exerciseId}/sets/{setId}")
    public ResponseEntity<Void> deleteSet(
            @PathVariable Long workoutId,
            @PathVariable Long exerciseId,
            @PathVariable Long setId) {
        log.info("Deleting set {} for exercise {} in workout {}", setId, exerciseId, workoutId);

        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        WorkoutExercise workoutExercise = workout.getWorkoutExercises().stream()
                .filter(we -> we.getId().equals(exerciseId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Exercise not found in workout with id: " + exerciseId));

        workoutExercise.getSets().removeIf(s -> s.getId().equals(setId));

        workoutRepository.save(workout);
        log.info("Set deleted successfully");

        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/workouts/{workoutId}/exercises/{exerciseId}/sets/{setId}/complete
     * Complete set
     */
    @PostMapping("/{exerciseId}/sets/{setId}/complete")
    public ResponseEntity<WorkoutSetDTO> completeSet(
            @PathVariable Long workoutId,
            @PathVariable Long exerciseId,
            @PathVariable Long setId) {
        log.info("Completing set {} for exercise {} in workout {}", setId, exerciseId, workoutId);

        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        WorkoutExercise workoutExercise = workout.getWorkoutExercises().stream()
                .filter(we -> we.getId().equals(exerciseId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Exercise not found in workout with id: " + exerciseId));

        WorkoutSet workoutSet = workoutExercise.getSets().stream()
                .filter(s -> s.getId().equals(setId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Set not found with id: " + setId));

        workoutSet.setCompleted(true);
        workoutSet.setCompletedAt(LocalDateTime.now());

        Workout savedWorkout = workoutRepository.save(workout);

        // Update statistics
        statisticsComputationService.updateStatisticsFromSet(workoutSet);

        WorkoutSetDTO dto = mapToWorkoutSetDTO(workoutSet);
        log.info("Set completed successfully");

        return ResponseEntity.ok(dto);
    }

    // Helper Methods
    private WorkoutExerciseDTO mapToWorkoutExerciseDTO(WorkoutExercise workoutExercise) {
        return WorkoutExerciseDTO.builder()
                .id(workoutExercise.getId())
                .exerciseId(workoutExercise.getExercise().getId())
                .exerciseName(workoutExercise.getExercise().getMetadata() != null ?
                        workoutExercise.getExercise().getMetadata().getName() : "")
                .exerciseDescription(workoutExercise.getExercise().getMetadata() != null ?
                        workoutExercise.getExercise().getMetadata().getDescription() : "")
                .primaryMuscleGroups(workoutExercise.getExercise().getPrimaryMuscleGroups())
                .secondaryMuscleGroups(workoutExercise.getExercise().getSecondaryMuscleGroups())
                .orderIndex(workoutExercise.getOrderIndex())
                .notes(workoutExercise.getNotes())
                .personalRecordAchieved(workoutExercise.isPersonalRecordAchieved())
                .sets(workoutExercise.getSets() != null ?
                        workoutExercise.getSets().stream()
                                .map(this::mapToWorkoutSetDTO)
                                .collect(Collectors.toList()) : List.of())
                .build();
    }

    private WorkoutSetDTO mapToWorkoutSetDTO(WorkoutSet workoutSet) {
        return WorkoutSetDTO.builder()
                .id(workoutSet.getId())
                .setNumber(workoutSet.getSetNumber())
                .setType(workoutSet.getSetType())
                .reps(workoutSet.getSetData() != null ? workoutSet.getSetData().getReps() : null)
                .weight(workoutSet.getSetData() != null ? workoutSet.getSetData().getWeight() : null)
                .distance(workoutSet.getSetData() != null ? workoutSet.getSetData().getDistance() : null)
                .duration(workoutSet.getSetData() != null ? workoutSet.getSetData().getDuration() : null)
                .restTime(workoutSet.getSetData() != null ? workoutSet.getSetData().getRestTime() : null)
                .notes(workoutSet.getSetData() != null ? workoutSet.getSetData().getNotes() : null)
                .isCompleted(workoutSet.isCompleted())
                .completedAt(workoutSet.getCompletedAt())
                .build();
    }
}
