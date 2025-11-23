package com.danis.ktrack.dto;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.valueobject.*;
import com.danis.ktrack.dto.CreateUserRequest;
import com.danis.ktrack.dto.UpdateUserRequest;
import com.danis.ktrack.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for converting between User entity and DTOs
 */
@Component
public class UserMapper {

    /**
     * Convert User entity to UserDTO
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedDate(user.getCreatedDate());

        // Map physical profile
        if (user.getPhysicalProfile() != null) {
            dto.setPhysicalProfile(toPhysicalProfileDTO(user.getPhysicalProfile()));
        }

        // Map preferences
        if (user.getPreferences() != null) {
            dto.setPreferences(toPreferencesDTO(user.getPreferences()));
        }

        return dto;
    }

    /**
     * Convert CreateUserRequest to User entity
     */
    public User toEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setCreatedDate(LocalDateTime.now());

        return user;
    }

    /**
     * Update existing User entity from UpdateUserRequest
     */
    public void updateEntityFromRequest(User user, UpdateUserRequest request) {
        if (user == null || request == null) {
            return;
        }

        // Update basic fields if provided
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        // Update or create physical profile
        UserPhysicalProfile profile = user.getPhysicalProfile();
        if (profile == null && hasPhysicalProfileData(request)) {
            profile = new UserPhysicalProfile();
            user.setPhysicalProfile(profile);
        }

        if (profile != null) {
            updatePhysicalProfile(profile, request);
        }

        // Update or create preferences
        UserPreferences preferences = user.getPreferences();
        if (preferences == null && hasPreferencesData(request)) {
            preferences = new UserPreferences();
            user.setPreferences(preferences);
        }

        if (preferences != null) {
            updatePreferences(preferences, request);
        }
    }

    private UserDTO.PhysicalProfileDTO toPhysicalProfileDTO(UserPhysicalProfile profile) {
        UserDTO.PhysicalProfileDTO dto = new UserDTO.PhysicalProfileDTO();

        if (profile.getWeight() != null) {
            dto.setWeightValue(profile.getWeight().getValue());
            dto.setWeightUnit(profile.getWeight().getUnit());
        }

        if (profile.getHeight() != null) {
            dto.setHeightValue(profile.getHeight().getValue());
            dto.setHeightUnit(profile.getHeight().getUnit());
        }

        dto.setAge(profile.getAge());
        dto.setGender(profile.getGender());

        if (profile.getBodyFatPercentage() != null) {
            dto.setBodyFatPercentage(profile.getBodyFatPercentage().getValue());
        }

        dto.setActivityLevel(profile.getActivityLevel());
        dto.setRecordedAt(profile.getRecordedAt());

        return dto;
    }

    private UserDTO.UserPreferencesDTO toPreferencesDTO(UserPreferences preferences) {
        UserDTO.UserPreferencesDTO dto = new UserDTO.UserPreferencesDTO();
        dto.setDefaultWeightUnit(preferences.getDefaultWeightUnit());
        dto.setDefaultHeightUnit(preferences.getDefaultHeightUnit());
        dto.setTheme(preferences.getTheme());
        return dto;
    }

    private void updatePhysicalProfile(UserPhysicalProfile profile, UpdateUserRequest request) {
        if (request.getWeightValue() != null) {
            Weight weight = profile.getWeight();
            if (weight == null) {
                weight = new Weight();
                profile.setWeight(weight);
            }
            weight.setValue(request.getWeightValue());
            if (request.getWeightUnit() != null) {
                weight.setUnit(request.getWeightUnit());
            }
        }

        if (request.getHeightValue() != null) {
            Height height = profile.getHeight();
            if (height == null) {
                height = new Height();
                profile.setHeight(height);
            }
            height.setValue(request.getHeightValue());
            if (request.getHeightUnit() != null) {
                height.setUnit(request.getHeightUnit());
            }
        }

        if (request.getAge() != null) {
            profile.setAge(request.getAge());
        }

        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }

        if (request.getBodyFatPercentage() != null) {
            BodyFatPercentage bfp = new BodyFatPercentage(request.getBodyFatPercentage());
            profile.setBodyFatPercentage(bfp);
        }

        if (request.getActivityLevel() != null) {
            profile.setActivityLevel(request.getActivityLevel());
        }

        profile.setRecordedAt(LocalDateTime.now());
    }

    private void updatePreferences(UserPreferences preferences, UpdateUserRequest request) {
        if (request.getDefaultWeightUnit() != null) {
            preferences.setDefaultWeightUnit(request.getDefaultWeightUnit());
        }
        if (request.getDefaultHeightUnit() != null) {
            preferences.setDefaultHeightUnit(request.getDefaultHeightUnit());
        }
        if (request.getTheme() != null) {
            preferences.setTheme(request.getTheme());
        }
    }

    private boolean hasPhysicalProfileData(UpdateUserRequest request) {
        return request.getWeightValue() != null ||
                request.getHeightValue() != null ||
                request.getAge() != null ||
                request.getGender() != null ||
                request.getBodyFatPercentage() != null ||
                request.getActivityLevel() != null;
    }

    private boolean hasPreferencesData(UpdateUserRequest request) {
        return request.getDefaultWeightUnit() != null ||
                request.getDefaultHeightUnit() != null ||
                request.getTheme() != null;
    }
}