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
class UserWorkflowServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private UserWorkflowServiceImpl userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setUsername("testuser");
        validUser.setEmail("test@example.com");
    }


    @Test
    void registerUser_Success_WhenUserIsValid() {

        doNothing().when(userValidationService).validate(any(User.class));

        when(userRepository.save(any(User.class))).thenReturn(validUser);


        User savedUser = userService.registerUser(validUser);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());

        verify(userValidationService, times(1)).validate(validUser);

        verify(userRepository, times(1)).save(validUser);
    }

    @Test
    void registerUser_ThrowsValidationException_WhenValidationFails() {

        doThrow(new ValidationException(List.of("Username cannot be empty")))
                .when(userValidationService).validate(validUser);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.registerUser(validUser)
        );

        assertTrue(exception.getMessage().contains("Username cannot be empty"));

        verify(userValidationService, times(1)).validate(validUser);

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void updateUserProfile_Success_WhenDataIsValid() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("olduser");
        existingUser.setEmail("old@example.com");

        User updatedDetails = new User();
        updatedDetails.setUsername("newuser");
        updatedDetails.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        doNothing().when(userValidationService).validate(updatedDetails);


        when(userRepository.save(existingUser)).thenReturn(existingUser);


        User result = userService.updateUserProfile(userId, updatedDetails);


        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());

        verify(userValidationService, times(1)).validate(updatedDetails);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUserProfile_ThrowsValidationException_WhenValidationFails() {
        Long userId = 1L;
        User invalidDetails = new User();
        invalidDetails.setEmail("bad-email-format");

        doThrow(new ValidationException(List.of("Email format is not valid")))
                .when(userValidationService).validate(invalidDetails);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.updateUserProfile(userId, invalidDetails)
        );

        assertTrue(exception.getMessage().contains("Email format is not valid"));

        verify(userValidationService, times(1)).validate(invalidDetails);

        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }
}