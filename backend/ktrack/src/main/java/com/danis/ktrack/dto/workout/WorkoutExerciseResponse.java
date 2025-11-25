package com.danis.ktrack.dto.workout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseResponse {

    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private int orderIndex;
    private String notes;
    private boolean personalRecordAchieved;

    private List<WorkoutSetResponse> sets;
}