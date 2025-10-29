package com.danis.ktrack.service.validation;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.Workout;
import com.danis.ktrack.domain.model.enums.WorkoutStatus;
import com.danis.ktrack.domain.model.valueobject.WorkoutPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutValidationServiceImplTest {

    private WorkoutValidationService workoutValidationService;
    private Workout workout;

    @BeforeEach
    void setUp() {
        workoutValidationService = new WorkoutValidationServiceImpl();

        // Create a complete, valid workout
        User testUser = new User();
        testUser.setId(1L);

        WorkoutPeriod validPeriod = new WorkoutPeriod();
        validPeriod.setStartTime(LocalDateTime.now());

        workout = new Workout();
        workout.setName("Morning Routine");
        workout.setDate(LocalDate.now());
        workout.setStatus(WorkoutStatus.IN_PROGRESS);
        workout.setPeriod(validPeriod);
        workout.setUser(testUser);
        workout.setTotalVolume(0); // Valid starting volume
        workout.setWorkoutExercises(new ArrayList<>()); // Valid empty list
    }

    @Test
    void validate_NullWorkout_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            workoutValidationService.validate(null);
        });
        assertTrue(exception.getMessage().contains("Workout cannot be null."));
    }

    @Test
    void validate_NullName_ThrowsValidationException() {
        workout.setName(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            workoutValidationService.validate(workout);
        });
        assertTrue(exception.getMessage().contains("Workout name cannot be empty."));
    }

    @Test
    void validate_BlankName_ThrowsValidationException() {
        workout.setName("   "); // Blank name
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            workoutValidationService.validate(workout);
        });
        assertTrue(exception.getMessage().contains("Workout name cannot be empty."));
    }

    @Test
    void validate_NullDate_ThrowsValidationException() {
        workout.setDate(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            workoutValidationService.validate(workout);
        });
        assertTrue(exception.getMessage().contains("Workout date must be specified."));
    }

    @Test
    void validate_NullStatus_ThrowsValidationException() {
        workout.setStatus(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            workoutValidationService.validate(workout);
        });
        assertTrue(exception.getMessage().contains("Workout status must be specified."));
    }

    @Test
    void validate_NullPeriod_ThrowsValidationException() {
        workout.setPeriod(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            workoutValidationService.validate(workout);
        });
        assertTrue(exception.getMessage().contains("Workout period cannot be null."));
    }

    @Test
    void validate_NullUser_ThrowsValidationException() {
        workout.setUser(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            workoutValidationService.validate(workout);
        });
        assertTrue(exception.getMessage().contains("Workout must be associated with a user."));
    }

    @Test
    void validate_NegativeTotalVolume_ThrowsValidationException() {
        workout.setTotalVolume(-100); // Negative volume
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            workoutValidationService.validate(workout);
        });
        assertTrue(exception.getMessage().contains("Total volume cannot be negative."));
    }

    @Test
    void validate_NullWorkoutExercises_ThrowsValidationException() {
        workout.setWorkoutExercises(null); // Null list
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            workoutValidationService.validate(workout);
        });
        assertTrue(exception.getMessage().contains("Workout exercises list cannot be null"));
    }

    @Test
    void validate_ValidWorkout_DoesNotThrowException() {
        // The 'workout' from setUp is already valid
        assertDoesNotThrow(() -> {
            workoutValidationService.validate(workout);
        });
    }

    @Test
    void validate_MultipleErrors_ThrowsValidationExceptionWithAllMessages() {
        workout.setName(null);
        workout.setDate(null);
        workout.setUser(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            workoutValidationService.validate(workout);
        });

        String message = exception.getMessage();
        assertTrue(message.contains("Workout name cannot be empty."));
        assertTrue(message.contains("Workout date must be specified."));
        assertTrue(message.contains("Workout must be associated with a user."));
        assertTrue(message.contains("Validation failed with 3 error(s)"));
    }
}