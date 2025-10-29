package com.danis.ktrack.service.validation;

import com.danis.ktrack.domain.model.entities.User;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceImplTest {

    @InjectMocks
    private UserValidationServiceImpl userValidationService;

    private User user;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");


    }

    @Test
    void validate_NullUser_ThrowsValidationException() {

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidationService.validate(null)
        );

        assertTrue(exception.getMessage().contains("User cannot be null"));
    }

    @Test
    void validate_NullUsername_ThrowsValidationException() {

        user.setUsername(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidationService.validate(user)
        );

        assertTrue(exception.getMessage().contains("Username cannot be empty."));
    }

    @Test
    void validate_BlankUsername_ThrowsValidationException() {
        user.setUsername("   ");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidationService.validate(user)
        );

        assertTrue(exception.getMessage().contains("Username cannot be empty."));
    }

    @Test
    void validate_InvalidEmailFormat_ThrowsValidationException() {
        user.setEmail("not-an-email");


        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidationService.validate(user)
        );

        assertTrue(exception.getMessage().contains("Email format is not valid."));
    }

    @Test
    void validate_NullEmail_ThrowsValidationException() {
        user.setEmail(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userValidationService.validate(user)
        );

        assertTrue(exception.getMessage().contains("Email cannot be empty."));
    }


    @Test
    void validate_ValidUser_DoesNotThrowException() {

        assertDoesNotThrow(() -> userValidationService.validate(user));
    }
}