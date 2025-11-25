package com.danis.ktrack.restservices;

import com.danis.ktrack.domain.model.entities.Workout;
import com.danis.ktrack.domain.model.enums.WorkoutStatus;
import com.danis.ktrack.dto.workout.WorkoutCreateRequest;
import com.danis.ktrack.dto.workout.WorkoutUpdateRequest;
import com.danis.ktrack.dto.workout.WorkoutResponse;
import com.danis.ktrack.dto.workout.WorkoutSummaryResponse;
import com.danis.ktrack.service.workflow.WorkoutWorkflowService;
import com.danis.ktrack.service.computation.WorkoutComputationService;
import com.danis.ktrack.domain.repository.WorkoutRepository;
// Linia 'import com.danis.ktrack.mapper.WorkoutMapper;' a fost ELIMINATĂ, deoarece clasa WorkoutMapper este în același pachet.
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
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutWorkflowService workoutWorkflowService;
    private final WorkoutComputationService workoutComputationService;
    private final WorkoutRepository workoutRepository;
    private final WorkoutMapper workoutMapper; // Rezolvat

    /**
     * POST /api/workouts - Start new workout
     */
    @PostMapping
    public ResponseEntity<WorkoutResponse> startWorkout(@Valid @RequestBody WorkoutCreateRequest request) {
        log.info("Starting new workout for user: {}", request.getUserId());

        Workout workout = workoutMapper.toEntity(request);
        Workout savedWorkout = workoutWorkflowService.startWorkout(workout, request.getUserId());

        WorkoutResponse response = workoutMapper.toResponse(savedWorkout);
        log.info("Workout created successfully with ID: {}", savedWorkout.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/workouts/{id} - Get workout details
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponse> getWorkoutById(@PathVariable Long id) {
        log.info("Fetching workout with ID: {}", id);

        Workout workout = workoutRepository.findByIdWithExercises(id)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + id));

        WorkoutResponse response = workoutMapper.toResponse(workout);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/workouts/{id} - Update workout
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponse> updateWorkout(
            @PathVariable Long id,
            @Valid @RequestBody WorkoutUpdateRequest request) {
        log.info("Updating workout with ID: {}", id);

        Workout workoutDetails = workoutMapper.toEntity(request);
        Workout updatedWorkout = workoutWorkflowService.updateWorkout(id, workoutDetails);

        WorkoutResponse response = workoutMapper.toResponse(updatedWorkout);
        log.info("Workout updated successfully with ID: {}", id);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/workouts/{id} - Delete workout
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        log.info("Deleting workout with ID: {}", id);

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + id));

        workoutRepository.delete(workout);
        log.info("Workout deleted successfully with ID: {}", id);

        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/workouts/{id}/complete - Complete workout
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<WorkoutResponse> completeWorkout(@PathVariable Long id) {
        log.info("Completing workout with ID: {}", id);

        Workout workout = workoutRepository.findByIdWithExercises(id)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + id));

        // Set completion details
        workout.setStatus(WorkoutStatus.COMPLETED);
        if (workout.getPeriod() != null) {
            workout.getPeriod().setEndTime(LocalDateTime.now());
        }

        // Calculate total volume
        workoutComputationService.calculateTotalVolume(workout);

        Workout savedWorkout = workoutRepository.save(workout);
        WorkoutResponse response = workoutMapper.toResponse(savedWorkout);

        log.info("Workout completed successfully with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/workouts/user/{userId} - Get user's workouts
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WorkoutResponse>> getUserWorkouts(@PathVariable Long userId) {
        log.info("Fetching workouts for user ID: {}", userId);

        List<Workout> workouts = workoutRepository.findAll().stream()
                .filter(w -> w.getUser() != null && w.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        List<WorkoutResponse> responses = workouts.stream()
                .map(workoutMapper::toResponse)
                .collect(Collectors.toList());

        log.info("Found {} workouts for user ID: {}", responses.size(), userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/workouts/{id}/summary - Get workout summary
     */
    @GetMapping("/{id}/summary")
    public ResponseEntity<WorkoutSummaryResponse> getWorkoutSummary(@PathVariable Long id) {
        log.info("Fetching summary for workout ID: {}", id);

        Workout workout = workoutRepository.findByIdWithExercises(id)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + id));

        WorkoutSummaryResponse summary = workoutMapper.toSummaryResponse(workout);
        return ResponseEntity.ok(summary);
    }
}