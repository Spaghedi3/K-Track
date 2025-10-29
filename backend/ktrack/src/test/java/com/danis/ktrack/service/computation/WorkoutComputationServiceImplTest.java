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

        Weight weight1 = new Weight(100.0, MeasurementUnit.KG);
        WorkoutSetData data1 = new WorkoutSetData();
        data1.setWeight(weight1);
        data1.setReps(10);
        WorkoutSet set1 = new WorkoutSet();
        set1.setSetData(data1);

        Weight weight2 = new Weight(110.0, MeasurementUnit.KG);
        WorkoutSetData data2 = new WorkoutSetData();
        data2.setWeight(weight2);
        data2.setReps(8);
        WorkoutSet set2 = new WorkoutSet();
        set2.setSetData(data2);

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
        computationService.calculateTotalVolume(workout);
        assertEquals(2480.0, workout.getTotalVolume());
    }

    @Test
    void calculateTotalVolume_HandlesEmptyExerciseList() {
        workout.setWorkoutExercises(List.of());
        workout.setTotalVolume(1000);
        computationService.calculateTotalVolume(workout);
        assertEquals(0.0, workout.getTotalVolume());
    }

    @Test
    void calculateTotalVolume_HandlesNullExerciseList() {
        workout.setWorkoutExercises(null);
        workout.setTotalVolume(1000);
        computationService.calculateTotalVolume(workout);
        assertEquals(0.0, workout.getTotalVolume());
    }

    @Test
    void calculateTotalVolume_HandlesSetWithNullSetData() {
        Weight weight1 = new Weight(100.0, MeasurementUnit.KG);
        WorkoutSetData data1 = new WorkoutSetData();
        data1.setWeight(weight1);
        data1.setReps(10);
        WorkoutSet set1 = new WorkoutSet();
        set1.setSetData(data1);

        WorkoutSet set2 = new WorkoutSet();
        set2.setSetData(null);

        WorkoutExercise exercise = new WorkoutExercise();
        exercise.setSets(List.of(set1, set2));
        workout.setWorkoutExercises(List.of(exercise));

        computationService.calculateTotalVolume(workout);
        assertEquals(1000.0, workout.getTotalVolume());
    }

    @Test
    void calculateTotalVolume_HandlesSetWithNullWeight() {
        Weight weight1 = new Weight(100.0, MeasurementUnit.KG);
        WorkoutSetData data1 = new WorkoutSetData();
        data1.setWeight(weight1);
        data1.setReps(10);
        WorkoutSet set1 = new WorkoutSet();
        set1.setSetData(data1);

        WorkoutSetData data2 = new WorkoutSetData();
        data2.setWeight(null);
        data2.setReps(10);
        WorkoutSet set2 = new WorkoutSet();
        set2.setSetData(data2);

        WorkoutExercise exercise = new WorkoutExercise();
        exercise.setSets(List.of(set1, set2));
        workout.setWorkoutExercises(List.of(exercise));

        computationService.calculateTotalVolume(workout);
        assertEquals(1000.0, workout.getTotalVolume());
    }

    @Test
    void calculateTotalVolume_HandlesSetWithNullReps() {
        Weight weight1 = new Weight(100.0, MeasurementUnit.KG);
        WorkoutSetData data1 = new WorkoutSetData();
        data1.setWeight(weight1);
        data1.setReps(10);
        WorkoutSet set1 = new WorkoutSet();
        set1.setSetData(data1);

        Weight weight2 = new Weight(100.0, MeasurementUnit.KG);
        WorkoutSetData data2 = new WorkoutSetData();
        data2.setWeight(weight2);
        data2.setReps(null);
        WorkoutSet set2 = new WorkoutSet();
        set2.setSetData(data2);

        WorkoutExercise exercise = new WorkoutExercise();
        exercise.setSets(List.of(set1, set2));
        workout.setWorkoutExercises(List.of(exercise));

        computationService.calculateTotalVolume(workout);
        assertEquals(1000.0, workout.getTotalVolume());
    }
}
