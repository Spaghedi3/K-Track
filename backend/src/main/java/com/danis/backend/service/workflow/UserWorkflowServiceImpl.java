package com.danis.backend.service.workflow;

import com.danis.backend.domain.model.entities.User;
import com.danis.backend.domain.repository.UserRepository;
import com.danis.backend.exception.EmailAlreadyExistsException;
import com.danis.backend.service.validation.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserWorkflowServiceImpl implements UserWorkflowService {

    private final UserValidationService userValidationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) {
        // 1. Validate
        userValidationService.validateUser(user);

        // 2. Check email uniqueness
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        // 3. Hash password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 4. Save user
        return userRepository.save(user);
    }
}