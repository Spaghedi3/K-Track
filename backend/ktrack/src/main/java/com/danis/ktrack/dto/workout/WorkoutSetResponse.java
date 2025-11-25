package com.danis.ktrack.dto.workout;

import com.danis.ktrack.domain.model.enums.SetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSetResponse {

    private Long id;
    private int setNumber;
    private SetType setType;

    private Integer reps;
    private Double weight;
    private String weightUnit;

    private Double distance;
    private String distanceUnit;

    private Long duration;
    private String durationUnit;

    private boolean isCompleted;
    private LocalDateTime completedAt;

    private String notes;
}