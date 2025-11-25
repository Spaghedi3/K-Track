package com.danis.ktrack.dto.exercise;

import com.danis.ktrack.domain.model.enums.ExerciseCategory;
import com.danis.ktrack.domain.model.enums.ExerciseType;
import com.danis.ktrack.domain.model.enums.MuscleGroup;
import com.danis.ktrack.domain.model.enums.PRType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExerciseRequest {
    @NotBlank(message = "Exercise name is required")
    private String name;

    private String description;
    private String instructions;
    private String videoUrl;
    private String imageUrl;

    @NotEmpty(message = "At least one primary muscle group is required")
    private List<MuscleGroup> primaryMuscleGroups;

    private List<MuscleGroup> secondaryMuscleGroups;

    @NotNull(message = "Exercise category is required")
    private ExerciseCategory category;

    @NotNull(message = "Exercise type is required")
    private ExerciseType type;
}
