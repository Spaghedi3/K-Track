package com.danis.backend.service.workflow;

import com.danis.backend.domain.model.entities.*;
import com.danis.backend.domain.model.enums.WorkoutStatus;
import com.danis.backend.domain.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    public Workout createFromTemplate(User user, WorkoutTemplate template) {
        Workout workout = Workout.builder()
                .user(user)
                .template(template)
                .status(WorkoutStatus.STARTED)
                .startedAt(LocalDateTime.now())
                .build();

        template.getExercises().forEach(te -> {
            WorkoutExercise we = WorkoutExercise.builder()
                    .workout(workout)
                    .exercise(te.getExercise())
                    .orderIndex(te.getOrderIndex())
                    .build();

            te.getSets().forEach(ts -> {
                we.getSets().add(
                        WorkoutSet.builder()
                                .workoutExercise(we)
                                .plannedReps(ts.getReps())
                                .plannedWeight(ts.getWeight())
                                .durationSeconds(ts.getDurationSeconds())
                                .distance(ts.getDistance())
                                .completed(false)
                                .build()
                );
            });

            workout.getExercises().add(we);
        });

        return workoutRepository.save(workout);
    }

    public Workout finish(Workout workout) {
        workout.setStatus(WorkoutStatus.COMPLETED);
        workout.setFinishedAt(LocalDateTime.now());
        return workout;
    }
}
