package com.danis.backend.dto;


import com.danis.backend.domain.model.enums.ActivityLevel;
import com.danis.backend.domain.model.enums.Gender;
import com.danis.backend.domain.model.enums.Goal;
import lombok.Data;

@Data
public class UserProfileDTO {
    private Integer age;
    private Gender gender;
    private Double height;
    private Double weight;
    private Goal goal;
    private ActivityLevel activityLevel;
}