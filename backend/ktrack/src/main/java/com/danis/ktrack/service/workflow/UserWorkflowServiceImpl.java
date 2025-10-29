package com.danis.ktrack.service.workflow;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.repository.UserRepository; // Assuming you have this repository
import com.danis.ktrack.service.validation.UserValidationService;
import com.danis.ktrack.service.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserWorkflowServiceImpl implements UserWorkflowService {

    private final UserRepository userRepository;
    private final UserValidationService userValidationService;

    // Dependencies are injected via the constructor
    public UserWorkflowServiceImpl(UserRepository userRepository,
                                   UserValidationService userValidationService) {
        this.userRepository = userRepository;
        this.userValidationService = userValidationService;
    }

    /**
     * Use Case: User Registration (Sign Up)
     */
    @Override
    @Transactional
    public User registerUser(User newUser) throws ValidationException {
        // 1. VALIDATION
        // Call the validation service you provided.
        // If validation fails, it throws a ValidationException and stops here.
        userValidationService.validate(newUser);

        // 2. REAL-WORLD LOGIC (See note below)
        // - Check if username/email already exists
        // - Hash the user's password

        // 3. PERSISTENCE
        // If validation passed, save the user.
        return userRepository.save(newUser);
    }

    /**
     * Use Case: Update User Profile
     */
    @Override
    @Transactional
    public User updateUserProfile(Long userId, User userDetails) throws ValidationException {
        // 1. VALIDATION
        // Validate the *new* details first.
        // If the new email is " ", this will fail before we query the database.
        userValidationService.validate(userDetails);

        // 2. FIND ENTITY
        // Find the existing user from the database.
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 3. REAL-WORLD LOGIC (See note below)
        // - Check if the new email/username is already taken by *another* user

        // 4. UPDATE & PERSIST
        // Update the fields on the existing entity
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());

        // Save the updated entity
        return userRepository.save(existingUser);
    }
}