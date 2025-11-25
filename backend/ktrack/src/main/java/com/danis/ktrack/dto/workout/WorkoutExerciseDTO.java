package com.danis.ktrack.dto.workout;

import com.danis.ktrack.domain.model.enums.MuscleGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseDTO {

    private Long id;
    
    // Exercise Details
    private Long exerciseId;
    private String exerciseName;
    private String exerciseDescription;
    private List<MuscleGroup> primaryMuscleGroups;
    private List<MuscleGroup> secondaryMuscleGroups;
    
    // Exercise Position in Workout
    private int orderIndex;
    private String notes;
    private boolean personalRecordAchieved;
    
    // Sets
    private List<WorkoutSetDTO> sets;
}
