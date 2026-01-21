package com.danis.backend.dto;

import com.danis.backend.domain.model.enums.ActivityLevel;
import com.danis.backend.domain.model.enums.Gender;
import com.danis.backend.domain.model.enums.Goal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user profile
 * All fields are optional - only provided fields will be updated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateDTO {
    private String fullName;
    private Integer age;
    private Gender gender;
    private Double height;
    private Double weight;
    private Goal goal;
    private ActivityLevel activityLevel;
}