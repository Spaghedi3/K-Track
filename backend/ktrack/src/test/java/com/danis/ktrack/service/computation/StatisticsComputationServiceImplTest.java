package com.danis.ktrack.service.computation;

import com.danis.ktrack.domain.model.entities.*;
import com.danis.ktrack.domain.model.enums.MeasurementUnit;
import com.danis.ktrack.domain.model.enums.PRType;
import com.danis.ktrack.domain.model.valueobject.PersonalRecord;
import com.danis.ktrack.domain.model.valueobject.VolumeMetrics;
import com.danis.ktrack.domain.model.valueobject.Weight;
import com.danis.ktrack.domain.model.valueobject.WorkoutSetData;
import com.danis.ktrack.domain.repository.ExerciseStatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsComputationServiceImplTest {

    @Mock
    private ExerciseStatisticsRepository statsRepository;

    @InjectMocks
    private StatisticsComputationServiceImpl statisticsComputationService;

    @Captor
    private ArgumentCaptor<ExerciseStatistics> statsCaptor;

    private User user;
    private Exercise exercise;
    private Workout workout;
    private WorkoutSet completedSet;
    private LocalDateTime completedTime;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        exercise = new Exercise();
        exercise.setId(1L);
        exercise.setMetadata(null); // Assuming ExerciseMetadata VO

        workout = new Workout();
        workout.setId(1L);
        workout.setUser(user);

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setId(1L);
        workoutExercise.setWorkout(workout);
        workoutExercise.setExercise(exercise);

        completedTime = LocalDateTime.now();
        completedSet = new WorkoutSet();
        completedSet.setId(1L);
        completedSet.setWorkoutExercise(workoutExercise);
        completedSet.setCompleted(true);
        completedSet.setCompletedAt(completedTime);

        // Set: 5 reps @ 100 kg
        WorkoutSetData setData = new WorkoutSetData();
        setData.setReps(5);
        setData.setWeight(new Weight(100.0, MeasurementUnit.KG)); // Using primitive double
        completedSet.setSetData(setData);
    }

    @Test
    void testUpdateStatistics_NewStats() {
        // Arrange
        when(statsRepository.findByUserAndExercise(user, exercise)).thenReturn(Optional.empty());

        // Act
        statisticsComputationService.updateStatisticsFromSet(completedSet);

        // Assert
        verify(statsRepository).save(statsCaptor.capture());
        ExerciseStatistics savedStats = statsCaptor.getValue();

        assertNotNull(savedStats);
        assertEquals(user, savedStats.getUser());
        assertEquals(exercise, savedStats.getExercise());
        assertEquals(1, savedStats.getTotalSets());
        assertEquals(5, savedStats.getTotalReps());
        assertEquals(500.0, savedStats.getTotalVolume());
        assertEquals(completedTime, savedStats.getLastPerformed());

        // Check Volume History
        assertEquals(1, savedStats.getVolumeHistory().size());
        VolumeDataPoint dp = savedStats.getVolumeHistory().get(0);
        assertEquals(workout, dp.getWorkout());
        assertEquals(completedTime.toLocalDate(), dp.getDate());

        // Check VolumeMetrics
        VolumeMetrics metrics = dp.getMetrics();
        assertNotNull(metrics);
        assertEquals(500.0, metrics.getTotalVolume());
        assertEquals(1, metrics.getTotalSets());
        assertEquals(5, metrics.getTotalReps());
        assertNull(metrics.getWeight()); // We don't set this

        // Check PRs (Now only checks 1RM)
        assertEquals(1, savedStats.getPersonalRecords().size());
        PersonalRecord pr1RM = savedStats.getPersonalRecords().get(0);

        // Epley for 5 @ 100 = 100 * (1 + 5/30) = 116.66...
        assertNotNull(pr1RM);
        assertEquals(PRType.MAX_WEIGHT, pr1RM.getType());
        assertEquals(116.66, pr1RM.getValue(), 0.01);
        assertEquals(completedTime, pr1RM.getAchievedDate());
        assertEquals(String.valueOf(exercise.getId()), pr1RM.getExerciseId());
        assertEquals(String.valueOf(workout.getId()), pr1RM.getWorkoutId());
    }

    @Test
    void testUpdateVolumeDataPoint_AggregatesSetVolume_ForSameWorkout() {
        // Arrange
        ExerciseStatistics existingStats = new ExerciseStatistics();
        existingStats.setUser(user);
        existingStats.setExercise(exercise);
        existingStats.setTotalSets(0);
        existingStats.setTotalReps(0);
        existingStats.setTotalVolume(0.0);
        existingStats.setPersonalRecords(new ArrayList<>());
        existingStats.setVolumeHistory(new ArrayList<>()); // Empty history

        when(statsRepository.findByUserAndExercise(user, exercise)).thenReturn(Optional.of(existingStats));

        // Create a second set for the *same workout*
        WorkoutSet set2 = new WorkoutSet();
        set2.setId(2L);
        set2.setWorkoutExercise(completedSet.getWorkoutExercise()); // Same WorkoutExercise
        set2.setCompleted(true);
        set2.setCompletedAt(completedTime.plusMinutes(2));
        set2.setSetData(completedSet.getSetData()); // Same data: 5 @ 100kg

        // Act 1: Process the first set
        statisticsComputationService.updateStatisticsFromSet(completedSet);

        // Assert 1: New VolumeDataPoint created
        assertEquals(1, existingStats.getVolumeHistory().size());
        VolumeDataPoint dp = existingStats.getVolumeHistory().get(0);
        assertEquals(500.0, dp.getMetrics().getTotalVolume());
        assertEquals(1, dp.getMetrics().getTotalSets());
        assertEquals(5, dp.getMetrics().getTotalReps());
        // Check overall stats
        assertEquals(1, existingStats.getTotalSets());
        assertEquals(500.0, existingStats.getTotalVolume());

        // Act 2: Process the second set
        statisticsComputationService.updateStatisticsFromSet(set2);

        // Assert 2: Existing VolumeDataPoint updated
        assertEquals(1, existingStats.getVolumeHistory().size()); // Should NOT create a new one
        VolumeMetrics metrics = dp.getMetrics();
        assertEquals(1000.0, metrics.getTotalVolume()); // 500 + 500
        assertEquals(2, metrics.getTotalSets());       // 1 + 1
        assertEquals(10, metrics.getTotalReps());      // 5 + 5

        // Check overall stats are also updated
        assertEquals(2, existingStats.getTotalSets());
        assertEquals(1000.0, existingStats.getTotalVolume());
        assertEquals(10, existingStats.getTotalReps());
    }

    @Test
    void testUpdateStatistics_ExistingStats_NewPR() {
        // Arrange
        LocalDateTime oldDate = completedTime.minusDays(7);
        ExerciseStatistics existingStats = new ExerciseStatistics();
        existingStats.setUser(user);
        existingStats.setExercise(exercise);
        existingStats.setTotalSets(10);
        existingStats.setTotalReps(100);
        existingStats.setTotalVolume(10000.0);
        existingStats.setPersonalRecords(new ArrayList<>(List.of(
                new PersonalRecord(String.valueOf(exercise.getId()), PRType.MAX_WEIGHT, 110.0, oldDate, "100")
        )));
        existingStats.setVolumeHistory(new ArrayList<>());

        when(statsRepository.findByUserAndExercise(user, exercise)).thenReturn(Optional.of(existingStats));

        // Act
        statisticsComputationService.updateStatisticsFromSet(completedSet); // 5 reps @ 100 kg (116.6 1RM)

        // Assert
        verify(statsRepository).save(existingStats);
        assertEquals(11, existingStats.getTotalSets());

        // Check PRs were updated
        assertEquals(1, existingStats.getPersonalRecords().size());
        PersonalRecord pr1RM = existingStats.getPersonalRecords().get(0);

        // New 1RM PR (116.6 > 110)
        assertNotNull(pr1RM);
        assertEquals(116.66, pr1RM.getValue(), 0.01);
        assertEquals(completedTime, pr1RM.getAchievedDate());
        assertEquals(String.valueOf(workout.getId()), pr1RM.getWorkoutId());
    }

    @Test
    void testUpdateStatistics_ExistingStats_NoNewPR() {
        // Arrange
        LocalDateTime oldDate = completedTime.minusDays(7);
        ExerciseStatistics existingStats = new ExerciseStatistics();
        existingStats.setUser(user);
        existingStats.setExercise(exercise);
        existingStats.setTotalSets(10);
        existingStats.setTotalReps(100);
        existingStats.setTotalVolume(10000.0);
        existingStats.setPersonalRecords(new ArrayList<>(List.of(
                new PersonalRecord(String.valueOf(exercise.getId()), PRType.MAX_WEIGHT, 120.0, oldDate, "100") // 120 > 116.6
        )));
        existingStats.setVolumeHistory(new ArrayList<>());

        when(statsRepository.findByUserAndExercise(user, exercise)).thenReturn(Optional.of(existingStats));

        // Act
        statisticsComputationService.updateStatisticsFromSet(completedSet); // 5 reps @ 100 kg

        // Assert
        verify(statsRepository).save(existingStats);

        // Totals are still updated
        assertEquals(11, existingStats.getTotalSets());
        assertEquals(105, existingStats.getTotalReps());
        assertEquals(10500.0, existingStats.getTotalVolume());

        // PRs are NOT updated
        PersonalRecord pr1RM = existingStats.getPersonalRecords().get(0);

        assertEquals(120.0, pr1RM.getValue());
        assertEquals(oldDate, pr1RM.getAchievedDate()); // Date is unchanged
        assertEquals("100", pr1RM.getWorkoutId()); // WorkoutID is unchanged
    }

    @Test
    void testIsWeightAndRepSet_Valid() {
        // This is effectively tested by testUpdateStatistics_NewStats, but good to be explicit
        WorkoutSetData setData = new WorkoutSetData();
        setData.setReps(5);
        setData.setWeight(new Weight(100.0, MeasurementUnit.KG));
        completedSet.setSetData(setData);

        // Act
        statisticsComputationService.updateStatisticsFromSet(completedSet);

        // Assert
        verify(statsRepository).save(any()); // Save was called
    }

    @Test
    void testIsWeightAndRepSet_Invalid_WeightObjectNull() {
        // Arrange
        completedSet.getSetData().setWeight(null); // Weight object is null

        // Act
        statisticsComputationService.updateStatisticsFromSet(completedSet);

        // Assert
        verify(statsRepository).save(statsCaptor.capture());
        ExerciseStatistics stats = statsCaptor.getValue();
        // Base stats are updated
        assertEquals(1, stats.getTotalSets());
        // Volume stats are NOT updated
        assertEquals(0, stats.getTotalReps());
        assertEquals(0.0, stats.getTotalVolume());
        assertEquals(0, stats.getPersonalRecords().size());
    }

    @Test
    void testIsWeightAndRepSet_Invalid_WeightValueIsZero() {
        // Arrange
        // This is a valid check for primitive doubles
        completedSet.getSetData().setWeight(new Weight(0.0, MeasurementUnit.KG));

        // Act
        statisticsComputationService.updateStatisticsFromSet(completedSet);

        // Assert
        verify(statsRepository).save(statsCaptor.capture());
        ExerciseStatistics stats = statsCaptor.getValue();
        // Base stats are updated
        assertEquals(1, stats.getTotalSets());
        // Volume stats are NOT updated
        assertEquals(0, stats.getTotalReps());
        assertEquals(0.0, stats.getTotalVolume());
        assertEquals(0, stats.getPersonalRecords().size());
    }

    @Test
    void testUpdateStatistics_InvalidSet_NotCompleted() {
        // Arrange
        completedSet.setCompleted(false);

        // Act
        statisticsComputationService.updateStatisticsFromSet(completedSet);

        // Assert
        verify(statsRepository, never()).findByUserAndExercise(any(), any());
        verify(statsRepository, never()).save(any());
    }

    @Test
    void testUpdateStatistics_InvalidSet_NullSet() {
        // Act
        statisticsComputationService.updateStatisticsFromSet(null);

        // Assert
        verify(statsRepository, never()).findByUserAndExercise(any(), any());
        verify(statsRepository, never()).save(any());
    }
}