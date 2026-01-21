package com.danis.backend.dto;

import com.danis.backend.domain.model.entities.Workout;
import com.danis.backend.domain.model.enums.WorkoutStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WorkoutResponse {
    private Long id;
    private WorkoutStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    public static WorkoutResponse from(Workout workout) {
        return WorkoutResponse.builder()
                .id(workout.getId())
                .status(WorkoutStatus.valueOf(workout.getStatus().name()))
                .startedAt(workout.getStartedAt())
                .finishedAt(workout.getFinishedAt())
                .build();
    }
}
