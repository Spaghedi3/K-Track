package com.danis.ktrack.service.validation;

import com.danis.ktrack.domain.model.entities.Exercise;
import com.danis.ktrack.domain.model.entities.ExerciseStatistics;
import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.VolumeDataPoint;
import com.danis.ktrack.domain.model.valueobject.PersonalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseStatisticsValidationServiceImplTest {
    private ExerciseStatisticsValidationService statsValidationService;
    private ExerciseStatistics stats;
    private User testUser;
    private Exercise testExercise;

    @BeforeEach
    void setUp() {
        statsValidationService = new ExerciseStatisticsValidationServiceImpl();

        testUser = new User();
        testExercise = new Exercise();

        stats = new ExerciseStatistics();
        stats.setUser(testUser);
        stats.setExercise(testExercise);
        stats.setPersonalRecords(new ArrayList<PersonalRecord>());
        stats.setVolumeHistory(new ArrayList<VolumeDataPoint>());
        stats.setTotalWorkouts(0);
        stats.setTotalSets(0);
        stats.setTotalReps(0);
        stats.setTotalVolume(0.0);
        stats.setLastPerformed(LocalDateTime.now().minusDays(1));
    }

    @Test
    void validate_NullStats_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            statsValidationService.validate(null);
        });
        assertTrue(exception.getMessage().contains("ExerciseStatistics cannot be null."));
    }

    @Test
    void validate_NullUser_ThrowsValidationException() {
        stats.setUser(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            statsValidationService.validate(stats);
        });
        assertTrue(exception.getMessage().contains("Statistics must be linked to a User."));
    }

    @Test
    void validate_NullExercise_ThrowsValidationException() {
        stats.setExercise(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            statsValidationService.validate(stats);
        });
        assertTrue(exception.getMessage().contains("Statistics must be linked to an Exercise."));
    }

    @Test
    void validate_NullPersonalRecordsList_ThrowsValidationException() {
        stats.setPersonalRecords(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            statsValidationService.validate(stats);
        });

        assertTrue(exception.getMessage().contains("Personal records list cannot be null (it can be empty).."));
    }

    @Test
    void validate_NullVolumeHistoryList_ThrowsValidationException() {
        stats.setVolumeHistory(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            statsValidationService.validate(stats);
        });
        System.out.print(exception.getMessage());
        assertTrue(exception.getMessage().contains("Volume history list cannot be null (it can be empty).."));
    }

    @Test
    void validate_NegativeTotalWorkouts_ThrowsValidationException() {
        stats.setTotalWorkouts(-1);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            statsValidationService.validate(stats);
        });
        assertTrue(exception.getMessage().contains("Total workouts cannot be negative."));
    }

    @Test
    void validate_NegativeTotalSets_ThrowsValidationException() {
        stats.setTotalSets(-5);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            statsValidationService.validate(stats);
        });
        assertTrue(exception.getMessage().contains("Total sets cannot be negative."));
    }

    @Test
    void validate_NegativeTotalReps_ThrowsValidationException() {
        stats.setTotalReps(-10);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            statsValidationService.validate(stats);
        });
        assertTrue(exception.getMessage().contains("Total reps cannot be negative."));
    }

    @Test
    void validate_NegativeTotalVolume_ThrowsValidationException() {
        stats.setTotalVolume(-100.5);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            statsValidationService.validate(stats);
        });
        assertTrue(exception.getMessage().contains("Total volume cannot be negative."));
    }

    @Test
    void validate_LastPerformedInFuture_ThrowsValidationException() {
        stats.setLastPerformed(LocalDateTime.now().plusDays(1));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            statsValidationService.validate(stats);
        });
        assertTrue(exception.getMessage().contains("Last performed date cannot be in the future."));
    }

    @Test
    void validate_ValidStats_DoesNotThrowException() {
        assertDoesNotThrow(() -> {
            statsValidationService.validate(stats);
        });
    }

    @Test
    void validate_ValidStatsWithNullLastPerformed_DoesNotThrowException() {
        stats.setLastPerformed(null);
        assertDoesNotThrow(() -> {
            statsValidationService.validate(stats);
        });
    }

    @Test
    void validate_MultipleErrors_ThrowsValidationExceptionWithAllMessages() {
        stats.setUser(null);
        stats.setExercise(null);
        stats.setTotalSets(-1);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            statsValidationService.validate(stats);
        });

        String message = exception.getMessage();
        assertTrue(message.contains("Statistics must be linked to a User."));
        assertTrue(message.contains("Statistics must be linked to an Exercise."));
        assertTrue(message.contains("Total sets cannot be negative."));
        assertTrue(message.contains("Validation failed with 3 error(s)"));
    }
}
