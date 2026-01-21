package com.danis.backend.dto;

import com.danis.backend.domain.model.entities.WorkoutTemplateExercise;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkoutTemplateExerciseResponseDTO {

    private Long exerciseId;
    private String exerciseName;
    private int orderIndex;
    private String imageUrl;
    private List<WorkoutTemplateSetResponseDTO> sets;

    public static WorkoutTemplateExerciseResponseDTO from(
            WorkoutTemplateExercise exercise
    ) {
        return WorkoutTemplateExerciseResponseDTO.builder()
                .exerciseId(exercise.getExercise().getId())
                .exerciseName(exercise.getExercise().getName())
                .orderIndex(exercise.getOrderIndex())
                .imageUrl(exercise.getExercise().getImageUrl())
                .sets(
                        exercise.getSets().stream()
                                .map(WorkoutTemplateSetResponseDTO::from)
                                .toList()
                )
                .build();
    }
}
