package com.danis.ktrack.service.computation;

import com.danis.ktrack.domain.model.entities.ExerciseStatistics;
import com.danis.ktrack.domain.model.entities.Workout;
import com.danis.ktrack.domain.model.entities.WorkoutSet;

public interface StaticticsComputationService {

    void updateStatisticsFromSet(WorkoutSet completedSet);}
