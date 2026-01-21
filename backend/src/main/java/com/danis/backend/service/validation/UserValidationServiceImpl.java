package com.danis.backend.service.validation;

import com.danis.backend.domain.model.entities.User;
import com.danis.backend.domain.model.enums.ActivityLevel;
import com.danis.backend.domain.model.enums.Gender;
import com.danis.backend.domain.model.enums.Goal;
import com.danis.backend.exception.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class UserValidationServiceImpl implements UserValidationService {

    @Override
    public void validateUser(User user) {
        if (user.getFullName() == null || user.getFullName().isEmpty()) {
            throw new ValidationException("Full name is required");
        }

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Email is required");
        }

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }

        // profile fields are optional here, but if present must be valid
        validateProfile(user);
    }

    @Override
    public void validateProfile(User user) {
        if (user.getAge() != null && user.getAge() <= 0) {
            throw new ValidationException("Age must be positive");
        }

        if (user.getHeight() != null && user.getHeight() <= 0) {
            throw new ValidationException("Height must be positive");
        }

        if (user.getWeight() != null && user.getWeight() <= 0) {
            throw new ValidationException("Weight must be positive");
        }

        if (user.getGender() != null && !isValidGender(user.getGender())) {
            throw new ValidationException("Invalid gender");
        }

        if (user.getGoal() != null && !isValidGoal(user.getGoal())) {
            throw new ValidationException("Invalid goal");
        }

        if (user.getActivityLevel() != null && !isValidActivityLevel(user.getActivityLevel())) {
            throw new ValidationException("Invalid activity level");
        }
    }

    private boolean isValidGender(Gender gender) {
        return gender == Gender.MALE || gender == Gender.FEMALE || gender == Gender.OTHER;
    }

    private boolean isValidGoal(Goal goal) {
        return goal == Goal.LOSE_WEIGHT || goal == Goal.MAINTAIN_WEIGHT || goal == Goal.GAIN_MUSCLE;
    }

    private boolean isValidActivityLevel(ActivityLevel activityLevel) {
        return activityLevel == ActivityLevel.LOW ||
                activityLevel == ActivityLevel.MEDIUM ||
                activityLevel == ActivityLevel.HIGH;
    }
}
