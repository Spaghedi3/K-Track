package com.danis.ktrack.dto.workout;

import com.danis.ktrack.domain.model.enums.SetType;
import com.danis.ktrack.domain.model.valueobject.Distance;
import com.danis.ktrack.domain.model.valueobject.Duration;
import com.danis.ktrack.domain.model.valueobject.Weight;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSetCreateDTO {

    @PositiveOrZero(message = "Set number must be zero or positive")
    private int setNumber;

    @NotNull(message = "Set type is required")
    private SetType setType;

    // Set Data - at least one metric should be provided
    private Integer reps;
    private Weight weight;
    private Distance distance;
    private Duration duration;
    private Duration restTime;
    private String notes;
}
