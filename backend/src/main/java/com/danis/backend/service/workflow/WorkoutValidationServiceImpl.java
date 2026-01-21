package com.danis.backend.service.workflow;

import com.danis.backend.domain.model.entities.User;
import com.danis.backend.domain.model.entities.Workout;
import com.danis.backend.domain.model.entities.WorkoutTemplate;
import com.danis.backend.domain.model.enums.WorkoutStatus;
import com.danis.backend.domain.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class WorkoutValidationServiceImpl implements WorkoutValidationService {

    private final WorkoutRepository workoutRepository;

    public void validateStart(User user, WorkoutTemplate template) {
        Optional<Workout> activeWorkout = workoutRepository
                .findFirstByUserIdAndStatusInOrderByStartedAtDesc(
                        user.getId(),
                        List.of(WorkoutStatus.STARTED, WorkoutStatus.PAUSED)
                );

        if (activeWorkout.isPresent()) {
            throw new IllegalStateException("You already have an active workout. Please finish or pause it first.");
        }
    }

    @Override
    public void validateFinish(Workout workout) {
        if (workout.getStatus() != WorkoutStatus.STARTED &&
                workout.getStatus() != WorkoutStatus.PAUSED) {
            throw new IllegalStateException("Can only finish workouts that are in progress or paused");
        }
    }
}
