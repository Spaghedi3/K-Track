package com.danis.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkoutExerciseResponse {
    private Long id;
    private String exerciseName;
    private String imageUrl;
    private Integer orderIndex;
    private List<WorkoutSetResponse> sets;
}