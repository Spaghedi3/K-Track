package com.danis.ktrack.service.validation;

import com.danis.ktrack.domain.model.entities.Exercise;
import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.enums.ExerciseCategory;
import com.danis.ktrack.domain.model.enums.ExerciseType;
import com.danis.ktrack.domain.model.enums.MuscleGroup;
import com.danis.ktrack.domain.model.valueobject.ExerciseMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class ExerciseValidationServiceImplTest {

private ExerciseValidationService exerciseValidationService;
private Exercise exercise;
private ExerciseMetadata validMetadata;
private User testUser;

@BeforeEach
    void setUp(){
    exerciseValidationService = new ExerciseValidationServiceImpl();

    validMetadata = new ExerciseMetadata();
    validMetadata.setName("Bench Press");
    validMetadata.setDescription("A chest exercise");

    testUser = new User();

    exercise = new Exercise();
    exercise.setMetadata(validMetadata);
    exercise.setPrimaryMuscleGroups(List.of(MuscleGroup.CHEST));
    exercise.setSecondaryMuscleGroups(new ArrayList<>());
    exercise.setCategory(ExerciseCategory.BARBELL);
    exercise.setType(ExerciseType.STRENGTH);
    exercise.setCustom(false);
    exercise.setCreatedBy(null);
}

    @Test
    void validate_NullExercise_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            exerciseValidationService.validate(null);
        });
        assertTrue(exception.getMessage().contains("Exercise cannot be null."));
    }
    @Test
    void validate_NullMetadata_ThrowsValidationException() {
        exercise.setMetadata(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            exerciseValidationService.validate(exercise);
        });
        assertTrue(exception.getMessage().contains("Exercise metadata cannot be null."));
    }
    @Test
    void validate_BlankMetadataName_ThrowsValidationException() {
        validMetadata.setName("  "); // Blank name
        exercise.setMetadata(validMetadata);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            exerciseValidationService.validate(exercise);
        });
        assertTrue(exception.getMessage().contains("Exercise name cannot be empty."));
    }
    @Test
    void validate_NullPrimaryMuscles_ThrowsValidationException() {
        exercise.setPrimaryMuscleGroups(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            exerciseValidationService.validate(exercise);
        });
        assertTrue(exception.getMessage().contains("Exercise must have at least one primary muscle group."));
    }
    @Test
    void validate_EmptyPrimaryMuscles_ThrowsValidationException() {
        exercise.setPrimaryMuscleGroups(new ArrayList<>()); // Empty list
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            exerciseValidationService.validate(exercise);
        });
        assertTrue(exception.getMessage().contains("Exercise must have at least one primary muscle group."));
    }
    @Test
    void validate_NullSecondaryMuscles_ThrowsValidationException() {
        exercise.setSecondaryMuscleGroups(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            exerciseValidationService.validate(exercise);
        });
        assertTrue(exception.getMessage().contains("Secondary muscle group list cannot be null"));
    }
    @Test
    void validate_ValidEmptySecondaryMuscles_DoesNotThrow() {
        exercise.setSecondaryMuscleGroups(new ArrayList<>()); // Empty is fine
        assertDoesNotThrow(() -> {
            exerciseValidationService.validate(exercise);
        });
    }
    @Test
    void validate_NullCategory_ThrowsValidationException() {
        exercise.setCategory(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            exerciseValidationService.validate(exercise);
        });
        assertTrue(exception.getMessage().contains("Exercise category must be specified."));
    }
    @Test
    void validate_NullType_ThrowsValidationException() {
        exercise.setType(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            exerciseValidationService.validate(exercise);
        });
        assertTrue(exception.getMessage().contains("Exercise type must be specified."));
    }
    @Test
    void validate_CustomExercise_NullUser_ThrowsValidationException() {
        exercise.setCustom(true);
        exercise.setCreatedBy(null); // This is now an error

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            exerciseValidationService.validate(exercise);
        });
        assertTrue(exception.getMessage().contains("A custom exercise must have a creator"));
    }
    @Test
    void validate_SystemExercise_NonNullUser_ThrowsValidationException() {
        exercise.setCustom(false);
        exercise.setCreatedBy(testUser); // This is now an error

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            exerciseValidationService.validate(exercise);
        });
        assertTrue(exception.getMessage().contains("A system exercise (isCustom=false) must not have a creator."));
    }
    @Test
    void validate_ValidCustomExercise_DoesNotThrow() {
        exercise.setCustom(true);
        exercise.setCreatedBy(testUser); // Valid combination
        assertDoesNotThrow(() -> {
            exerciseValidationService.validate(exercise);
        });
    }

    @Test
    void validate_ValidSystemExercise_DoesNotThrow() {
        exercise.setCustom(false);
        exercise.setCreatedBy(null); // Valid combination
        assertDoesNotThrow(() -> {
            exerciseValidationService.validate(exercise);
        });
    }
    @Test
    void validate_MultipleErrors_ThrowsValidationExceptionWithAllMessages() {
        exercise.setMetadata(null);
        exercise.setPrimaryMuscleGroups(null);
        exercise.setCategory(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            exerciseValidationService.validate(exercise);
        });

        String message = exception.getMessage();
        assertTrue(message.contains("Exercise metadata cannot be null."));
        assertTrue(message.contains("Exercise must have at least one primary muscle group."));
        assertTrue(message.contains("Exercise category must be specified."));
        assertTrue(message.contains("Validation failed with 3 error(s)"));
    }

}
