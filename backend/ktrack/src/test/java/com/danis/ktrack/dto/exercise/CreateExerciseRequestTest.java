package com.danis.ktrack.dto.exercise;

import com.danis.ktrack.domain.model.enums.ExerciseCategory;
import com.danis.ktrack.domain.model.enums.ExerciseType;
import com.danis.ktrack.domain.model.enums.MuscleGroup;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateExerciseRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validRequest_ShouldPassValidation() {
        CreateExerciseRequest request = new CreateExerciseRequest();
        request.setName("Push Up");
        request.setPrimaryMuscleGroups(List.of(MuscleGroup.CHEST));
        request.setCategory(ExerciseCategory.BODYWEIGHT);
        request.setType(ExerciseType.STRENGTH);

        Set<ConstraintViolation<CreateExerciseRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no validation errors for valid request");
    }

    @Test
    void nullName_ShouldFailValidation() {
        CreateExerciseRequest request = new CreateExerciseRequest();
        request.setName(null); // Invalid: @NotBlank
        request.setPrimaryMuscleGroups(List.of(MuscleGroup.CHEST));
        request.setCategory(ExerciseCategory.BODYWEIGHT);
        request.setType(ExerciseType.STRENGTH);

        Set<ConstraintViolation<CreateExerciseRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected validation errors for null name");

        boolean hasNameError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        assertTrue(hasNameError, "Should have validation error specifically on 'name' field");
    }

    @Test
    void emptyMuscleGroups_ShouldFailValidation() {
        CreateExerciseRequest request = new CreateExerciseRequest();
        request.setName("Test Exercise");
        request.setPrimaryMuscleGroups(List.of()); // Invalid: @NotEmpty
        request.setCategory(ExerciseCategory.BARBELL);
        request.setType(ExerciseType.STRENGTH);

        Set<ConstraintViolation<CreateExerciseRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasMuscleError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("primaryMuscleGroups"));
        assertTrue(hasMuscleError, "Should have error on 'primaryMuscleGroups' field");
    }

    @Test
    void nullCategory_ShouldFailValidation() {
        CreateExerciseRequest request = new CreateExerciseRequest();
        request.setName("Test Exercise");
        request.setPrimaryMuscleGroups(List.of(MuscleGroup.LEGS));
        request.setCategory(null); // Invalid: @NotNull
        request.setType(ExerciseType.STRENGTH);

        Set<ConstraintViolation<CreateExerciseRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasCategoryError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("category"));
        assertTrue(hasCategoryError, "Should have error on 'category' field");
    }
}