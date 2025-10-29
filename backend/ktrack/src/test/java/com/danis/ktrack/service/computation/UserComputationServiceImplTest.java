package com.danis.ktrack.service.computation;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.Workout;
import com.danis.ktrack.domain.model.enums.HeightUnit;
import com.danis.ktrack.domain.model.enums.MeasurementUnit;
import com.danis.ktrack.domain.model.valueobject.Height;
import com.danis.ktrack.domain.model.valueobject.UserPhysicalProfile;
import com.danis.ktrack.domain.model.valueobject.Weight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserComputationServiceImplTest {

    private UserComputationService computationService;
    private User testUser;

    private static class MockHeight extends Height {

        private final double centimeterValue;
        private final Double rawValue;

        public MockHeight(Double value, HeightUnit unit) {
            super(value, unit);
            this.rawValue = value;
            if (value == null) {
                this.centimeterValue = 0.0;
            } else if (unit == HeightUnit.CM) {
                this.centimeterValue = value;
            } else {
                this.centimeterValue = value * 2.54;
            }
        }

        @Override
        public Double getValue() { return rawValue; }

        @Override
        public double toCentimeters() {
            return centimeterValue;
        }
    }

    private static class MockWeight extends Weight {

        private final double kilogramValue;
        private final Double rawValue;

        public MockWeight(Double value, MeasurementUnit unit) {
            super(value, unit);
            this.rawValue = value;
            if (value == null) {
                this.kilogramValue = 0.0;
            } else if (unit.name().equals("KG")) {
                this.kilogramValue = value;
            } else {
                this.kilogramValue = value * 0.453592;
            }
        }

        @Override
        public Double getValue() { return rawValue; }

        @Override
        public double toKilograms() {
            return kilogramValue;
        }
    }

    @BeforeEach
    void setUp() {
        computationService = new UserComputationServiceImpl();
        testUser = new User();
        testUser.setWorkouts(new ArrayList<>());
        testUser.setProfileHistory(new ArrayList<>());
        Weight weight = new Weight(154.324, MeasurementUnit.KG);
    }

    @Test
    void calculateBmi_ValidProfile_CM_KG_ReturnsCorrectBmi() {
        UserPhysicalProfile profile = new UserPhysicalProfile();
        profile.setWeight(new MockWeight(70.0, MeasurementUnit.KG));
        profile.setHeight(new MockHeight(175.0, HeightUnit.CM));
        testUser.setPhysicalProfile(profile);
        assertEquals(22.86, computationService.calculateBmi(testUser), 0.01);
    }

    @Test
    void calculateBmi_ValidProfile_LBS_INCH_ReturnsCorrectBmi() {
        UserPhysicalProfile profile = new UserPhysicalProfile();
        profile.setWeight(new MockWeight(70.0, MeasurementUnit.KG));
        profile.setHeight(new MockHeight(175.0, HeightUnit.CM));
        testUser.setPhysicalProfile(profile);
        assertEquals(22.86, computationService.calculateBmi(testUser), 0.01, "Should correctly handle LBS and INCH conversion.");
    }

    @Test
    void calculateBmi_NullWeightValue_ReturnsNull() {
        UserPhysicalProfile profile = new UserPhysicalProfile();
        profile.setWeight(new MockWeight(null, MeasurementUnit.KG));
        profile.setHeight(new MockHeight(180.0, HeightUnit.CM));
        testUser.setPhysicalProfile(profile);
        assertNull(computationService.calculateBmi(testUser), "BMI should be null if the Weight value is null.");
    }

    @Test
    void calculateBmi_HeightValueIsZero_ReturnsNull() {
        UserPhysicalProfile profile = new UserPhysicalProfile();
        profile.setWeight(new MockWeight(80.0, MeasurementUnit.KG));
        profile.setHeight(new MockHeight(0.0, HeightUnit.CM));
        testUser.setPhysicalProfile(profile);
        assertNull(computationService.calculateBmi(testUser), "BMI should be null if height is zero (division by zero risk).");
    }

    @Test
    void getMostRecentWeight_ValidHistory_ReturnsLatestWeightInKg() {
        UserPhysicalProfile oldProfile = new UserPhysicalProfile();
        oldProfile.setRecordedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        oldProfile.setWeight(new MockWeight(90.0, MeasurementUnit.KG));
        UserPhysicalProfile newestProfile = new UserPhysicalProfile();
        newestProfile.setRecordedAt(LocalDateTime.of(2024, 10, 20, 10, 0));
        newestProfile.setWeight(new MockWeight(85.0, MeasurementUnit.KG));
        testUser.setProfileHistory(List.of(oldProfile, newestProfile));
        Double weight = computationService.getMostRecentWeight(testUser);
        System.out.println();
        assertEquals(85.0, weight, 0.01, "Should return the most recent weight, correctly converted to KG.");
    }

    @Test
    void getMostRecentWeight_EmptyHistory_ReturnsNull() {
        assertNull(computationService.getMostRecentWeight(testUser), "Most recent weight should be null if history is empty.");
    }

    @Test
    void getTotalWorkoutCount_MultipleWorkouts_ReturnsCorrectCount() {
        List<Workout> workouts = List.of(new Workout(), new Workout(), new Workout());
        testUser.setWorkouts(workouts);
        assertEquals(3, computationService.getTotalWorkoutCount(testUser), "Count should match the number of workouts.");
    }

    @Test
    void getTotalWorkoutCount_NullWorkoutsList_ReturnsZero() {
        testUser.setWorkouts(null);
        assertEquals(0, computationService.getTotalWorkoutCount(testUser), "Count should be 0 if the workout list is null.");
    }
}
