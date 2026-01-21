package com.danis.backend.restservices;


import com.danis.backend.config.JwtService;
import com.danis.backend.domain.model.entities.User;
import com.danis.backend.domain.repository.UserRepository;
import com.danis.backend.dto.*;
import com.danis.backend.exception.InvalidCredentialException;
import com.danis.backend.service.validation.UserValidationService;
import com.danis.backend.service.workflow.UserWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserWorkflowService userWorkflowService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserValidationService userValidationService;

    @PostMapping("/signup")
    public AuthResponseDTO signup(@RequestBody UserSignupDTO request) {

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User savedUser = userWorkflowService.registerUser(user);

        String token = jwtService.generateToken(
                savedUser.getId(),
                savedUser.getEmail()
        );

        return AuthResponseDTO.builder()
                .token(token)
                .build();
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody UserLoginDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail()
        );

        return AuthResponseDTO.builder()
                .token(token)
                .build();
    }

    /**
     * Get the current user's profile
     */
    @GetMapping("/profile")
    public UserProfileResponseDTO getProfile() {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .profilePictureUrl(user.getProfilePictureUrl())
                .age(user.getAge())
                .gender(user.getGender())
                .height(user.getHeight())
                .weight(user.getWeight())
                .goal(user.getGoal())
                .activityLevel(user.getActivityLevel())
                .build();
    }

    /**
     * Update user profile information
     */
    @PutMapping("/profile")
    public UserProfileResponseDTO updateProfile(@RequestBody UserProfileUpdateDTO profileDto) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update only provided fields
        if (profileDto.getFullName() != null) {
            user.setFullName(profileDto.getFullName());
        }
        if (profileDto.getAge() != null) {
            user.setAge(profileDto.getAge());
        }
        if (profileDto.getGender() != null) {
            user.setGender(profileDto.getGender());
        }
        if (profileDto.getHeight() != null) {
            user.setHeight(profileDto.getHeight());
        }
        if (profileDto.getWeight() != null) {
            user.setWeight(profileDto.getWeight());
        }
        if (profileDto.getGoal() != null) {
            user.setGoal(profileDto.getGoal());
        }
        if (profileDto.getActivityLevel() != null) {
            user.setActivityLevel(profileDto.getActivityLevel());
        }

        userValidationService.validateProfile(user);

        User updated = userRepository.save(user);

        return UserProfileResponseDTO.builder()
                .id(updated.getId())
                .fullName(updated.getFullName())
                .email(updated.getEmail())
                .profilePictureUrl(updated.getProfilePictureUrl())
                .age(updated.getAge())
                .gender(updated.getGender())
                .height(updated.getHeight())
                .weight(updated.getWeight())
                .goal(updated.getGoal())
                .activityLevel(updated.getActivityLevel())
                .build();
    }

    /**
     * Update user profile picture
     */
    @PutMapping("/profile-picture")
    public UserProfileResponseDTO updateProfilePicture(@RequestBody ProfilePictureDTO pictureDto) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfilePictureUrl(pictureDto.getProfilePictureUrl());

        User updated = userRepository.save(user);

        return UserProfileResponseDTO.builder()
                .id(updated.getId())
                .fullName(updated.getFullName())
                .email(updated.getEmail())
                .profilePictureUrl(updated.getProfilePictureUrl())
                .age(updated.getAge())
                .gender(updated.getGender())
                .height(updated.getHeight())
                .weight(updated.getWeight())
                .goal(updated.getGoal())
                .activityLevel(updated.getActivityLevel())
                .build();
    }

    /**
     * Complete profile (for initial profile setup)
     */
    @PutMapping("/complete-profile")
    public UserResponseDTO completeProfile(@RequestBody UserProfileDTO profileDto) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAge(profileDto.getAge());
        user.setGender(profileDto.getGender());
        user.setHeight(profileDto.getHeight());
        user.setWeight(profileDto.getWeight());
        user.setGoal(profileDto.getGoal());
        user.setActivityLevel(profileDto.getActivityLevel());

        userValidationService.validateProfile(user);

        User updated = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(updated.getId())
                .fullName(updated.getFullName())
                .email(updated.getEmail())
                .build();
    }
}