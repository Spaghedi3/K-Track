package com.danis.ktrack.service.validation;

import com.danis.ktrack.domain.model.entities.ExerciseStatistics;

public interface ExerciseStatisticsValidationService {
    void validate(ExerciseStatistics exerciseStatistics) throws ValidationException;
}
