package com.danis.ktrack.service.validation;

import com.danis.ktrack.domain.model.entities.WorkoutTemplate;

public interface WorkoutTemplateValidationService {

    void validate(WorkoutTemplate workoutTemplate) throws ValidationException;

}