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

        userValidationService.validate(newUser);


        return userRepository.save(newUser);
    }

    /**
     * Use Case: Update User Profile
     */
    @Override
    @Transactional
    public User updateUserProfile(Long userId, User userDetails) throws ValidationException {

        userValidationService.validate(userDetails);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));


        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());

        return userRepository.save(existingUser);
    }
}