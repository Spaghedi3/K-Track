package com.danis.ktrack.service.validation;

import com.danis.ktrack.domain.model.entities.TemplateExercise;
import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.WorkoutTemplate;
import com.danis.ktrack.domain.model.enums.TimeUnit;
import com.danis.ktrack.domain.model.valueobject.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutTemplateValidationServiceImplTest {

    private WorkoutTemplateValidationService validationService;
    private WorkoutTemplate validTemplate;
    private User testUser;

    @BeforeEach
    void setUp() {
        validationService = new WorkoutTemplateValidationServiceImpl();

        testUser = new User();
        // Assuming User has at least an ID set for non-null checks
        testUser.setId(1L);

        TemplateExercise templateExercise = new TemplateExercise();
        // Assuming TemplateExercise only needs to be non-null for this test

        validTemplate = new WorkoutTemplate();
        validTemplate.setId(1L);
        validTemplate.setName("Full Body Power");
        validTemplate.setDescription("A high-intensity workout.");
        validTemplate.setCreatedByUser(testUser);
        validTemplate.setTags(new ArrayList<>(List.of("full body", "power")));
        validTemplate.setEstimatedDuration(new Duration(60, TimeUnit.MINUTES));
        validTemplate.setTemplateExercises(List.of(templateExercise));
        validTemplate.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void validate_ValidTemplate_DoesNotThrow() {

        assertDoesNotThrow(() -> {
            validationService.validate(validTemplate);
        }, "A fully valid template should pass without exceptions.");
    }

    @Test
    void validate_NullTemplate_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validationService.validate(null);
        });
        assertTrue(exception.getMessage().contains("WorkoutTemplate cannot be null."), "Should specifically check for null object.");
    }

    @Test
    void validate_NullName_ThrowsValidationException() {
        validTemplate.setName(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validationService.validate(validTemplate);
        });
        assertTrue(exception.getMessage().contains("WorkoutTemplate name cannot be empty."), "Should check for null name.");
    }

    @Test
    void validate_BlankName_ThrowsValidationException() {
        validTemplate.setName("  \t"); // Blank string

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validationService.validate(validTemplate);
        });
        assertTrue(exception.getMessage().contains("WorkoutTemplate name cannot be empty."), "Should check for blank name.");
    }

    @Test
    void validate_NullcreatedByUser_ThrowsValidationException() {
        validTemplate.setCreatedBy(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validationService.validate(validTemplate);
        });
        assertTrue(exception.getMessage().contains("WorkoutTemplate must have a creator (createdByUser user cannot be null)."), "Should check for null creator.");
    }

    @Test
    void validate_NullTemplateExercises_ThrowsValidationException() {
        validTemplate.setTemplateExercises(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validationService.validate(validTemplate);
        });
        assertTrue(exception.getMessage().contains("WorkoutTemplate must contain at least one exercise."), "Should check for null exercises list.");
    }

    @Test
    void validate_EmptyTemplateExercises_ThrowsValidationException() {

        validTemplate.setTemplateExercises(new ArrayList<>());


        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validationService.validate(validTemplate);
        });
        assertTrue(exception.getMessage().contains("WorkoutTemplate must contain at least one exercise."), "Should check for empty exercises list.");
    }

    @Test
    void validate_NullTags_ThrowsValidationException() {

        validTemplate.setTags(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validationService.validate(validTemplate);
        });
        assertTrue(exception.getMessage().contains("WorkoutTemplate tags list cannot be null (it can be empty)."), "Should check if the list of tags is null.");
    }

    @Test
    void validate_EmptyTags_DoesNotThrow() {

        validTemplate.setTags(new ArrayList<>()); // Empty list of tags is valid

        assertDoesNotThrow(() -> {
            validationService.validate(validTemplate);
        }, "An empty list of tags is perfectly valid.");
    }

    @Test
    void validate_NullCreatedAt_ThrowsValidationException() {

        validTemplate.setCreatedAt(null);


        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validationService.validate(validTemplate);
        });
        assertTrue(exception.getMessage().contains("WorkoutTemplate creation date must be specified."), "Should check for null creation date.");
    }

    @Test
    void validate_MultipleErrors_ThrowsValidationExceptionWithAllMessages() {

        validTemplate.setName(null); // Error 1
        validTemplate.setCreatedByUser(null); // Error 2
        validTemplate.setTemplateExercises(null); // Error 3
        validTemplate.setTags(null); // Error 4
        validTemplate.setCreatedAt(null); // Error 5

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validationService.validate(validTemplate);
        });

        String message = exception.getMessage();
        System.out.println("Validation Errors: " + message);

        assertTrue(message.contains("WorkoutTemplate name cannot be empty."));
        assertTrue(message.contains("WorkoutTemplate must have a creator"));
        assertTrue(message.contains("WorkoutTemplate must contain at least one exercise."));
        assertTrue(message.contains("WorkoutTemplate tags list cannot be null"));
        assertTrue(message.contains("WorkoutTemplate creation date must be specified."));

        assertTrue(message.contains("Validation failed with 5 error(s)"), "Should count all expected errors.");
    }
}