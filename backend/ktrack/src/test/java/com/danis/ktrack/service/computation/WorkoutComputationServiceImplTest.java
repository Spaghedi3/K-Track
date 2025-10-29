package com.danis.ktrack.service.computation;

import com.danis.ktrack.domain.model.entities.Workout;
import com.danis.ktrack.domain.model.entities.WorkoutExercise;
import com.danis.ktrack.domain.model.entities.WorkoutSet;
import com.danis.ktrack.domain.model.enums.MeasurementUnit;
import com.danis.ktrack.domain.model.valueobject.Weight;
import com.danis.ktrack.domain.model.valueobject.WorkoutSetData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkoutComputationServiceImplTest {

    private WorkoutComputationService computationService;
    private Workout workout;

    @BeforeEach
    void setUp() {
        computationService = new WorkoutComputationServiceImpl();
        workout = new Workout();

        // Set 1: 100kg * 10 reps = 1000 volume
        Weight weight1 = new Weight(100.0, MeasurementUnit.KG);
        WorkoutSetData data1 = new WorkoutSetData();
        data1.setWeight(weight1);
        data1.setReps(10);
        WorkoutSet set1 = new WorkoutSet();
        set1.setSetData(data1);

        // Set 2: 110kg * 8 reps = 880 volume
        Weight weight2 = new Weight(110.0, MeasurementUnit.KG);
        WorkoutSetData data2 = new WorkoutSetData();
        data2.setWeight(weight2);
        data2.setReps(8);
        WorkoutSet set2 = new WorkoutSet();
        set2.setSetData(data2);

        // Set 3: 50kg * 12 reps = 600 volume
        Weight weight3 = new Weight(50.0, MeasurementUnit.KG);
        WorkoutSetData data3 = new WorkoutSetData();
        data3.setWeight(weight3);
        data3.setReps(12);
        WorkoutSet set3 = new WorkoutSet();
        set3.setSetData(data3);

        WorkoutExercise exercise1 = new WorkoutExercise();
        exercise1.setSets(List.of(set1, set2));

        WorkoutExercise exercise2 = new WorkoutExercise();
        exercise2.setSets(List.of(set3));

        workout.setWorkoutExercises(List.of(exercise1, exercise2));
    }

    @Test
    void calculateTotalVolume_CorrectlySumsAllSets() {
        // Act
        computationService.calculateTotalVolume(workout);

        // Assert
        // Expected: (100*10) + (110*8) + (50*12) = 1000 + 880 + 600 = 2480
        assertEquals(2480.0, workout.getTotalVolume());
    }

    @Test
    void calculateTotalVolume_HandlesEmptyExerciseList() {
        // Arrange
        workout.setWorkoutExercises(List.of());
        workout.setTotalVolume(1000); // Set a non-zero start value

        // Act
        computationService.calculateTotalVolume(workout);

        // Assert
        assertEquals(0.0, workout.getTotalVolume());
    }

    @Test
    void calculateTotalVolume_HandlesNullExerciseList() {
        // Arrange
        workout.setWorkoutExercises(null);
        workout.setTotalVolume(1000); // Set a non-zero start value

        // Act
        computationService.calculateTotalVolume(workout);

        // Assert
        assertEquals(0.0, workout.getTotalVolume());
    }

    @Test
    void calculateTotalVolume_HandlesSetWithNullSetData() {
        // Arrange
        Weight weight1 = new Weight(100.0, MeasurementUnit.KG);
        WorkoutSetData data1 = new WorkoutSetData();
        data1.setWeight(weight1);
        data1.setReps(10); // 1000
        WorkoutSet set1 = new WorkoutSet();
        set1.setSetData(data1);

        WorkoutSet set2 = new WorkoutSet();
        set2.setSetData(null); // SetData itself is null, should be skipped

        WorkoutExercise exercise = new WorkoutExercise();
        exercise.setSets(List.of(set1, set2));
        workout.setWorkoutExercises(List.of(exercise));

        // Act
        computationService.calculateTotalVolume(workout);

        // Assert
        assertEquals(1000.0, workout.getTotalVolume());
    }

    @Test
    void calculateTotalVolume_HandlesSetWithNullWeight() {
        // Arrange
        // Set 1: Valid
        Weight weight1 = new Weight(100.0, MeasurementUnit.KG);
        WorkoutSetData data1 = new WorkoutSetData();
        data1.setWeight(weight1);
        data1.setReps(10); // 1000
        WorkoutSet set1 = new WorkoutSet();
        set1.setSetData(data1);

        // Set 2: Null weight
        WorkoutSetData data2 = new WorkoutSetData();
        data2.setWeight(null); // Weight object is null
        data2.setReps(10);
        WorkoutSet set2 = new WorkoutSet();
        set2.setSetData(data2);

        WorkoutExercise exercise = new WorkoutExercise();
        exercise.setSets(List.of(set1, set2));
        workout.setWorkoutExercises(List.of(exercise));

        // Act
        computationService.calculateTotalVolume(workout);

        // Assert
        assertEquals(1000.0, workout.getTotalVolume());
    }

    @Test
    void calculateTotalVolume_HandlesSetWithNullReps() {
        // Arrange
        // Set 1: Valid
        Weight weight1 = new Weight(100.0, MeasurementUnit.KG);
        WorkoutSetData data1 = new WorkoutSetData();
        data1.setWeight(weight1);
        data1.setReps(10); // 1000
        WorkoutSet set1 = new WorkoutSet();
        set1.setSetData(data1);

        // Set 2: Null reps
        Weight weight2 = new Weight(100.0, MeasurementUnit.KG);
        WorkoutSetData data2 = new WorkoutSetData();
        data2.setWeight(weight2);
        data2.setReps(null); // Reps is null
        WorkoutSet set2 = new WorkoutSet();
        set2.setSetData(data2);

        WorkoutExercise exercise = new WorkoutExercise();
        exercise.setSets(List.of(set1, set2));
        workout.setWorkoutExercises(List.of(exercise));

        // Act
        computationService.calculateTotalVolume(workout);

        // Assert
        assertEquals(1000.0, workout.getTotalVolume());
    }
}