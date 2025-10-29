package com.danis.ktrack.service.workflow;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.service.validation.ValidationException;

public interface UserService {

    /**
     * Use Case: User Registration (Sign Up)
     * Validates and creates a new user.
     *
     * @param newUser User object with username, email, and password.
     * @return The persisted User entity.
     * @throws ValidationException if the user data is invalid.
     */
    User registerUser(User newUser) throws ValidationException;

    /**
     * Use Case: Update User Profile
     * Validates and updates an existing user's profile information.
     *
     * @param userId      The ID of the user to update.
     * @param userDetails User object with the new details (e.g., username, email).
     * @return The updated User entity.
     * @throws ValidationException if the new user data is invalid.
     * @throws RuntimeException    if the user is not found.
     */
    User updateUserProfile(Long userId, User userDetails) throws ValidationException;
}