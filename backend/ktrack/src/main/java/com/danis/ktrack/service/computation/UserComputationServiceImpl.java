package com.danis.ktrack.service.computation;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.valueobject.Height;
import com.danis.ktrack.domain.model.valueobject.UserPhysicalProfile;
import com.danis.ktrack.domain.model.valueobject.Weight;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class UserComputationServiceImpl implements UserComputationService {

    /**
     * BMI Formula: weight (kg) / height (m)^2
     */
    @Override
    public Double calculateBmi(User user) {
        if (user == null || user.getPhysicalProfile() == null) {
            return null;
        }

        UserPhysicalProfile profile = user.getPhysicalProfile();
        Weight weight = profile.getWeight();
        Height height = profile.getHeight();

        if (weight == null || height == null) {
            return null;
        }

        Double weightValueObj = weight.getValue();
        Double heightValueObj = height.getValue();

        if (weightValueObj == null || heightValueObj == null) {
            return null;
        }

        double weightValue = weightValueObj;
        double heightValue = heightValueObj;

        if (weightValue <= 0 || heightValue <= 0) {
            return null;
        }

        double heightInMeters = heightValue / 100.0;
        double bmi = weightValue / (heightInMeters * heightInMeters);

        return Math.round(bmi * 100.0) / 100.0;
    }


    /**
     * Retrieves the most recent weight recorded in the user's profile history.
     */
    @Override
    public Double getMostRecentWeight(User user) {
        if (user == null || user.getProfileHistory() == null || user.getProfileHistory().isEmpty()) {
            return null;
        }

        Optional<UserPhysicalProfile> latestProfile = user.getProfileHistory().stream()
                .max(Comparator.comparing(UserPhysicalProfile::getRecordedAt));

        return latestProfile
                .map(UserPhysicalProfile::getWeight)
                .map(Weight::getValue)  // nu folosim toKilograms()
                .orElse(null);
    }

    /**
     * Calculates the total number of workouts completed by the user.
     */
    @Override
    public int getTotalWorkoutCount(User user) {
        List<?> workouts = user != null ? user.getWorkouts() : null;
        return workouts != null ? workouts.size() : 0;
    }
}
