package com.danis.backend.service.workflow;

import com.danis.backend.domain.model.entities.*;
import com.danis.backend.domain.model.enums.WorkoutStatus;
import com.danis.backend.domain.repository.WorkoutRepository;
import com.danis.backend.domain.repository.WorkoutTemplateRepository;
import com.danis.backend.dto.*;
import com.danis.backend.service.workflow.WorkoutService;
import com.danis.backend.service.workflow.WorkoutValidationService;
import com.danis.backend.service.workflow.WorkoutWorkflowService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutWorkflowServiceImpl implements WorkoutWorkflowService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final WorkoutService workoutService;
    private final WorkoutValidationService workoutValidationService;

    @Override
    public Workout startWorkout(Long userId, Long templateId) {
        WorkoutTemplate template = workoutTemplateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template not found"));

        User user = template.getUser();

        workoutValidationService.validateStart(user, template);

        return workoutService.createFromTemplate(user, template);
    }

    @Override
    public Workout finishWorkout(Long workoutId, Long userId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new EntityNotFoundException("Workout not found"));

        // ownership check
        if (!workout.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You cannot finish someone else's workout");
        }

        workoutValidationService.validateFinish(workout);
        return workoutService.finish(workout);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutDetailResponse getWorkoutDetails(Long workoutId, Long userId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new EntityNotFoundException("Workout not found"));

        if (!workout.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You cannot view someone else's workout");
        }

        return mapToDetail(workout);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkoutDetailResponse> getActiveWorkout(Long userId) {
        return workoutRepository
                .findFirstByUserIdAndStatusInOrderByStartedAtDesc(
                        userId,
                        List.of(WorkoutStatus.STARTED, WorkoutStatus.PAUSED)
                )
                .map(this::mapToDetail);
    }

    @Override
    public WorkoutSetResponse updateWorkoutSet(
            Long workoutId,
            Long exerciseId,
            Long setId,
            Long userId,
            UpdateWorkoutSetRequest request
    ) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new EntityNotFoundException("Workout not found"));

        if (!workout.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You cannot update someone else's workout");
        }

        workoutValidationService.validateFinish(workout); // throws if not STARTED

        WorkoutExercise exercise = workout.getExercises().stream()
                .filter(e -> e.getId().equals(exerciseId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

        WorkoutSet set = exercise.getSets().stream()
                .filter(s -> s.getId().equals(setId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Set not found"));

        set.setActualReps(request.getActualReps());
        set.setActualWeight(request.getActualWeight());
        set.setCompleted(request.isCompleted());

        workoutRepository.save(workout); // cascading saves all

        return mapToSetResponse(set, exercise);
    }

    // ----------------------
    // Mapping helpers
    // ----------------------

    private WorkoutDetailResponse mapToDetail(Workout workout) {
        List<WorkoutExerciseResponse> exercises = workout.getExercises().stream()
                .sorted(Comparator.comparingInt(WorkoutExercise::getOrderIndex))
                .map(this::mapToExerciseResponse)
                .collect(Collectors.toList());

        return WorkoutDetailResponse.builder()
                .id(workout.getId())
                .templateName(workout.getTemplate().getName())
                .status(workout.getStatus())
                .startedAt(workout.getStartedAt())
                .finishedAt(workout.getFinishedAt())
                .exercises(exercises)
                .build();
    }

    private WorkoutExerciseResponse mapToExerciseResponse(WorkoutExercise exercise) {
        List<WorkoutSetResponse> sets = exercise.getSets().stream()
                .map(set -> mapToSetResponse(set, exercise))
                .collect(Collectors.toList());

        return WorkoutExerciseResponse.builder()
                .id(exercise.getId())
                .exerciseName(exercise.getExercise().getName())
                .imageUrl(exercise.getExercise().getImageUrl())
                .orderIndex(exercise.getOrderIndex())
                .sets(sets)
                .build();
    }

    private WorkoutSetResponse mapToSetResponse(WorkoutSet set, WorkoutExercise exercise) {
        int setNumber = exercise.getSets().indexOf(set) + 1;

        return WorkoutSetResponse.builder()
                .id(set.getId())
                .setNumber(setNumber)
                .plannedReps(set.getPlannedReps())
                .plannedWeight(set.getPlannedWeight())
                .actualReps(set.getActualReps())
                .actualWeight(set.getActualWeight())
                .completed(set.isCompleted())
                .build();
    }
    // WorkoutWorkflowServiceImpl.java

    @Override
    public Workout pauseWorkout(Long workoutId, Long userId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new EntityNotFoundException("Workout not found"));

        if (!workout.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You cannot pause someone else's workout");
        }

        if (workout.getStatus() != WorkoutStatus.STARTED) {
            throw new IllegalStateException("Can only pause a workout that is in progress");
        }

        workout.setStatus(WorkoutStatus.PAUSED);
        return workoutRepository.save(workout);
    }

    @Override
    public Workout resumeWorkout(Long workoutId, Long userId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new EntityNotFoundException("Workout not found"));

        if (!workout.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You cannot resume someone else's workout");
        }

        if (workout.getStatus() != WorkoutStatus.PAUSED) {
            throw new IllegalStateException("Can only resume a paused workout");
        }

        workout.setStatus(WorkoutStatus.STARTED);
        return workoutRepository.save(workout);
    }
}
