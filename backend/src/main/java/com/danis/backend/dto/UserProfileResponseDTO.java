package com.danis.backend.dto;

import com.danis.backend.domain.model.enums.ActivityLevel;
import com.danis.backend.domain.model.enums.Gender;
import com.danis.backend.domain.model.enums.Goal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning complete user profile information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String profilePictureUrl;
    private Integer age;
    private Gender gender;
    private Double height;
    private Double weight;
    private Goal goal;
    private ActivityLevel activityLevel;
}