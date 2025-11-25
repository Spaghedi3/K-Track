package com.danis.ktrack.service;

import com.danis.ktrack.dto.exercise.ExerciseDTO;
import com.danis.ktrack.dto.exercise.ExerciseStatisticsRequest;
import com.danis.ktrack.dto.exercise.ExerciseSummaryRequest;
import java.util.List;

public interface ExerciseService {
    List<ExerciseSummaryRequest> getAllExercises();
    ExerciseDTO getExerciseById(Long id);
    ExerciseDTO createExercise(ExerciseDTO exerciseDTO);
    void deleteExercise(Long id);
    ExerciseStatisticsRequest getExerciseStatistics(Long exerciseId, Long userId);
}