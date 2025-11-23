package com.danis.ktrack.dto;

import com.danis.ktrack.domain.model.enums.ActivityLevel;
import com.danis.ktrack.domain.model.enums.Gender;
import com.danis.ktrack.domain.model.enums.HeightUnit;
import com.danis.ktrack.domain.model.enums.MeasurementUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for User entity - used for responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdDate;

    // Physical Profile
    private PhysicalProfileDTO physicalProfile;

    // Preferences
    private UserPreferencesDTO preferences;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhysicalProfileDTO {
        private Double weightValue;
        private MeasurementUnit weightUnit;
        private Double heightValue;
        private HeightUnit heightUnit;
        private Integer age;
        private Gender gender;
        private Double bodyFatPercentage;
        private ActivityLevel activityLevel;
        private LocalDateTime recordedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPreferencesDTO {
        private MeasurementUnit defaultWeightUnit;
        private HeightUnit defaultHeightUnit;
        private String theme;
    }
}