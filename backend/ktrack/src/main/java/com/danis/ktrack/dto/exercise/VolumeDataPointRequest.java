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
public class VolumeDataPointRequest {
    private Long id;
    private LocalDate date;
    private Double totalVolume;
    private Integer totalSets;
    private Integer totalReps;
    private Integer weight;
    private Long workoutId;
}
