package com.danis.ktrack.service.computation;

import com.danis.ktrack.domain.model.entities.Workout;

public interface WorkoutComputationService {

    /**
     * Calculates the total volume for a given workout based on its
     * exercises and sets.
     * The method updates the 'totalVolume' field on the Workout object directly.
     *
     * @param workout The workout entity to compute.
     * @return
     */
    double calculateTotalVolume(Workout workout);

}