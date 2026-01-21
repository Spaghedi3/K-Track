package com.danis.backend.service.workflow;

import com.danis.backend.domain.model.entities.User;
import com.danis.backend.domain.model.entities.Workout;
import com.danis.backend.domain.model.entities.WorkoutTemplate;

public interface WorkoutValidationService {

    void validateStart(User user, WorkoutTemplate template);

    void validateFinish(Workout workout);

}
