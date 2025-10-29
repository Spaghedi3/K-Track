package com.danis.ktrack.service.computation;

import com.danis.ktrack.domain.model.entities.User;

public interface UserComputationService {

    /**
     * Calculates the Body Mass Index (BMI) for the user based on their current physical profile.
     * @param user The user entity.
     * @return The calculated BMI, or null if weight or height is missing.
     */
    Double calculateBmi(User user);

    /**
     * Retrieves the most recent weight recorded in the user's profile history.
     * @param user The user entity.
     * @return The most recent weight in kilograms, or null if history is empty.
     */
    Double getMostRecentWeight(User user);

    /**
     * Calculates the total number of workouts completed by the user.
     * @param user The user entity.
     * @return The count of workouts, or 0 if the list is null or empty.
     */
    int getTotalWorkoutCount(User user);
}