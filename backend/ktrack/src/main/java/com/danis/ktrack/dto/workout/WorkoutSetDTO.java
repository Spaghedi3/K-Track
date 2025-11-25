package com.danis.ktrack.dto.workout;

import com.danis.ktrack.domain.model.enums.SetType;
import com.danis.ktrack.domain.model.valueobject.Distance;
import com.danis.ktrack.domain.model.valueobject.Duration;
import com.danis.ktrack.domain.model.valueobject.Weight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSetDTO {

    private Long id;
    private int setNumber;
    private SetType setType;

    // Set Data
    private Integer reps;
    private Weight weight;
    private Distance distance;
    private Duration duration;
    private Duration restTime;
    private String notes;

    // Completion Status
    private boolean isCompleted;
    private LocalDateTime completedAt;
}
