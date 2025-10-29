package com.danis.ktrack.service.workflow;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.repository.UserRepository;
import com.danis.ktrack.service.validation.UserValidationService;
import com.danis.ktrack.service.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private UserServiceImpl userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setUsername("testuser");
        validUser.setEmail("test@example.com");
    }

    // --- User Registration (Sign Up) Tests ---

    @Test
    void registerUser_Success_WhenUserIsValid() {
        // Arrange
        // Mock the validation to pass
        doNothing().when(userValidationService).validate(any(User.class));

        // Mock the repository save
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // Act
        User savedUser = userService.registerUser(validUser);

        // Assert
        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());

        // Verify that validation WAS called
        verify(userValidationService, times(1)).validate(validUser);

        // Verify that save WAS called
        verify(userRepository, times(1)).save(validUser);
    }

    @Test
    void registerUser_ThrowsValidationException_WhenValidationFails() {
        // Arrange
        // Mock the validation to fail and throw an exception
        doThrow(new ValidationException(List.of("Username cannot be empty")))
                .when(userValidationService).validate(validUser);

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.registerUser(validUser)
        );

        assertTrue(exception.getMessage().contains("Username cannot be empty"));

        // Verify that validation WAS called
        verify(userValidationService, times(1)).validate(validUser);

        // Verify that the repository was NEVER called
        verify(userRepository, never()).save(any(User.class));
    }

    // --- Update User Profile Tests ---

    @Test
    void updateUserProfile_Success_WhenDataIsValid() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("olduser");
        existingUser.setEmail("old@example.com");

        User updatedDetails = new User();
        updatedDetails.setUsername("newuser");
        updatedDetails.setEmail("new@example.com");

        // Mock finding the user
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Mock the validation to pass
        doNothing().when(userValidationService).validate(updatedDetails);

        // Mock the save call (it will return the modified existingUser)
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        User result = userService.updateUserProfile(userId, updatedDetails);

        // Assert
        assertNotNull(result);
        // Check that the fields were updated
        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());

        // Verify methods were called in order
        verify(userValidationService, times(1)).validate(updatedDetails);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUserProfile_ThrowsValidationException_WhenValidationFails() {
        // Arrange
        Long userId = 1L;
        User invalidDetails = new User();
        invalidDetails.setEmail("bad-email-format"); // Invalid data

        // Mock the validation to fail
        doThrow(new ValidationException(List.of("Email format is not valid")))
                .when(userValidationService).validate(invalidDetails);

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.updateUserProfile(userId, invalidDetails)
        );

        assertTrue(exception.getMessage().contains("Email format is not valid"));

        // Verify validation was called
        verify(userValidationService, times(1)).validate(invalidDetails);

        // Verify we never touched the database
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }
}