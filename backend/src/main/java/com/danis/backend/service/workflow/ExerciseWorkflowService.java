package com.danis.backend.service.workflow;


import com.danis.backend.domain.model.entities.Exercise;
import com.danis.backend.dto.ExerciseCreateDTO;

import java.util.List;

public interface ExerciseWorkflowService {
    Exercise createCustomExercise(ExerciseCreateDTO dto, Long userId);
    List<Exercise> getAllExercises();
    List<Exercise> getPreloadedExercises();
    List<Exercise> getCustomExercises(Long userId);
    void deleteCustomExercise(Long exerciseId, Long userId);
}