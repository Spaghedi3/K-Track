package com.danis.backend.restservices;

import com.danis.backend.dto.*;
import com.danis.backend.service.workflow.WorkoutWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutWorkflowService workoutWorkflowService;

    @PostMapping("/start")
    public WorkoutResponse startWorkout(@RequestBody StartWorkoutRequest request) {
        Long userId = getLoggedInUserId();
        return WorkoutResponse.from(
                workoutWorkflowService.startWorkout(userId, request.getTemplateId())
        );
    }

    @PostMapping("/{id}/finish")
    public WorkoutResponse finishWorkout(@PathVariable Long id) {
        Long userId = getLoggedInUserId();
        return WorkoutResponse.from(
                workoutWorkflowService.finishWorkout(id, userId)
        );
    }
    @GetMapping("/{id}")
    public WorkoutDetailResponse getWorkout(@PathVariable Long id) {
        Long userId = getLoggedInUserId();
        return workoutWorkflowService.getWorkoutDetails(id, userId);
    }

    @PutMapping("/{workoutId}/exercises/{exerciseId}/sets/{setId}")
    public WorkoutSetResponse updateSet(
            @PathVariable Long workoutId,
            @PathVariable Long exerciseId,
            @PathVariable Long setId,
            @RequestBody UpdateWorkoutSetRequest request
    ) {
        Long userId = getLoggedInUserId();
        return workoutWorkflowService.updateWorkoutSet(workoutId, exerciseId, setId, userId, request);
    }

    @GetMapping("/active")
    public ResponseEntity<WorkoutDetailResponse> getActiveWorkout() {
        Long userId = getLoggedInUserId();

        return workoutWorkflowService.getActiveWorkout(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    // WorkoutController.java
    @PostMapping("/{id}/pause")
    public WorkoutResponse pauseWorkout(@PathVariable Long id) {
        Long userId = getLoggedInUserId();
        return WorkoutResponse.from(
                workoutWorkflowService.pauseWorkout(id, userId)
        );
    }
    @PostMapping("/{id}/resume")
    public WorkoutResponse resumeWorkout(@PathVariable Long id) {
        Long userId = getLoggedInUserId();
        return WorkoutResponse.from(
                workoutWorkflowService.resumeWorkout(id, userId)
        );
    }
    private Long getLoggedInUserId() {
        return Long.parseLong(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );
    }

}
