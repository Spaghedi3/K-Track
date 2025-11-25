package com.danis.ktrack.dto.exercise;

import com.danis.ktrack.domain.model.enums.ExerciseCategory;
import com.danis.ktrack.domain.model.enums.ExerciseType;
import com.danis.ktrack.domain.model.enums.MuscleGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {
    private Long id;
    private String name;
    private String description;
    private String instructions;
    private String videoUrl;
    private String imageUrl;

    private List<MuscleGroup> primaryMuscleGroups;
    private List<MuscleGroup> secondaryMuscleGroups;
    private ExerciseCategory category;
    private ExerciseType type;
    private boolean isCustom;

    private Long createdByUserId;
    private String createdByUsername;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;
}