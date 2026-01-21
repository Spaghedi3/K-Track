package com.danis.backend.dto;

import com.danis.backend.domain.model.entities.WorkoutTemplateSet;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkoutTemplateSetResponseDTO {

    private Integer reps;
    private Double weight;
    private Integer durationSeconds;
    private Double distance;

    public static WorkoutTemplateSetResponseDTO from(
            WorkoutTemplateSet set
    ) {
        return WorkoutTemplateSetResponseDTO.builder()
                .reps(set.getReps())
                .weight(set.getWeight())
                .durationSeconds(set.getDurationSeconds())
                .distance(set.getDistance())
                .build();
    }
}
