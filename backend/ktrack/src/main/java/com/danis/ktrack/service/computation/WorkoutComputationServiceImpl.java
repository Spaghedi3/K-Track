package com.danis.ktrack.service.computation;

import com.danis.ktrack.domain.model.entities.Workout;
import com.danis.ktrack.domain.model.entities.WorkoutExercise;
import com.danis.ktrack.domain.model.entities.WorkoutSet;
import com.danis.ktrack.domain.model.valueobject.Weight;
import com.danis.ktrack.domain.model.valueobject.WorkoutSetData;
import org.springframework.stereotype.Service;

@Service
public class WorkoutComputationServiceImpl implements WorkoutComputationService {

    /**
     * Calculates total volume (sum of [weight * reps] for all sets).
     */
    @Override
    public void calculateTotalVolume(Workout workout) {
        if (workout == null) {
            return;
        }

        double totalVolume = 0.0;

        if (workout.getWorkoutExercises() != null) {
            for (WorkoutExercise exercise : workout.getWorkoutExercises()) {

                if (exercise.getSets() != null) {
                    for (WorkoutSet set : exercise.getSets()) {

                        WorkoutSetData setData = set.getSetData();

                        // Check for all required components
                        if (setData != null &&
                                setData.getWeight() != null &&
                                setData.getReps() != null) {

                            // Path: setData -> getWeight() -> getValue()
                            double weightValue = setData.getWeight().getValue();
                            int repsValue = setData.getReps();

                            totalVolume += (weightValue * repsValue);
                        }
                    }
                }
            }
        }

        workout.setTotalVolume(totalVolume);
    }
}