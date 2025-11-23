package com.danis.ktrack.dto;

import com.danis.ktrack.domain.model.enums.ActivityLevel;
import com.danis.ktrack.domain.model.enums.Gender;
import com.danis.ktrack.domain.model.enums.HeightUnit;
import com.danis.ktrack.domain.model.enums.MeasurementUnit;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user profile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    private String username;

    @Email(message = "Email must be valid")
    private String email;

    // Physical Profile Updates
    @Positive(message = "Weight must be positive")
    private Double weightValue;
    private MeasurementUnit weightUnit;

    @Positive(message = "Height must be positive")
    private Double heightValue;
    private HeightUnit heightUnit;

    @Positive(message = "Age must be positive")
    private Integer age;
    private Gender gender;

    @Positive(message = "Body fat percentage must be positive")
    private Double bodyFatPercentage;
    private ActivityLevel activityLevel;

    // Preferences
    private MeasurementUnit defaultWeightUnit;
    private HeightUnit defaultHeightUnit;
    private String theme;
}