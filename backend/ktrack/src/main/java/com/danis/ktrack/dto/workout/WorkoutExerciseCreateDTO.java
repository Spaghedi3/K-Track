package com.danis.ktrack.dto.workout;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseCreateDTO {

    @NotNull(message = "Exercise ID is required")
    private Long exerciseId;

    @Positive(message = "Order index must be positive")
    private int orderIndex;

    private String notes;
}
