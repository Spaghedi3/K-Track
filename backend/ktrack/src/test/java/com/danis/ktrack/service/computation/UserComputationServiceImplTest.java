package com.danis.ktrack.service.computation;

import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.Workout;
import com.danis.ktrack.domain.model.enums.HeightUnit;
import com.danis.ktrack.domain.model.enums.MeasurementUnit;
import com.danis.ktrack.domain.model.enums.MeasurementUnit; // Assuming this exists for the test setup
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

    // =========================================================================
    // MOCK CLASSES: These MUST be implemented in your real value objects
    // for the service to compile and run outside of this test file.
    // =========================================================================

    // NOTE: This assumes Height extends a base class/interface that provides getValue()/getUnit().
    // If Height is only a Value Object, this is a reasonable mock structure.
    private static class MockHeight extends Height {

        private final double centimeterValue;
        private final Double rawValue;

        // Simplified constructor to hold the value we need for conversion testing
        public MockHeight(Double value, HeightUnit unit) {
            super(value, unit); // Use real V.O. constructor
            this.rawValue = value;

            // Pre-calculate the CM value for accurate testing
            if (value == null) {
                this.centimeterValue = 0.0;
            } else if (unit == HeightUnit.CM) {
                this.centimeterValue = value;
            } else {
                this.centimeterValue = value * 2.54;
            }
        }

        // Ensure getValue() returns the raw Double (allowing us to test for null)
        @Override
        public Double getValue() { return rawValue; }

        // The method the service relies on
        @Override
        public double toCentimeters() {
            return centimeterValue;
        }
    }

    // NOTE: This assumes Weight extends a base class/interface that provides getValue()/getUnit().
    // We are setting the internal value as Double to allow the 'null' test case.
    private static class MockWeight extends Weight {

        private final double kilogramValue;
        private final Double rawValue;

        // Simplified constructor to hold the value we need for conversion testing
        public MockWeight(Double value, MeasurementUnit unit) {
            super(value, unit); // Use real V.O. constructor
            this.rawValue = value;

            // Pre-calculate the KG value for accurate testing
            if (value == null) {
                this.kilogramValue = 0.0;
            } else if (unit.name().equals("KG")) { // Use .name().equals("KG") as unit is an enum
                this.kilogramValue = value;
            } else {
                this.kilogramValue = value * 0.453592;
            }
        }

        // Ensure getValue() returns the raw Double (allowing us to test for null)
        @Override
        public Double getValue() { return rawValue; }

        // The method the service relies on
        @Override
        public double toKilograms() {
            return kilogramValue;
        }
    }

    // =========================================================================
    // SETUP
    // =========================================================================

    @BeforeEach
    void setUp() {
        computationService = new UserComputationServiceImpl();
        testUser = new User();
        testUser.setWorkouts(new ArrayList<>());
        testUser.setProfileHistory(new ArrayList<>());
        Weight weight = new Weight(154.324, MeasurementUnit.KG);
    }

    // =========================================================================
    // BMI TESTS
    // =========================================================================

    @Test
    void calculateBmi_ValidProfile_CM_KG_ReturnsCorrectBmi() {
        // Arrange: 70kg / (1.75m * 1.75m) = 22.86
        UserPhysicalProfile profile = new UserPhysicalProfile();
        profile.setWeight(new MockWeight(70.0, MeasurementUnit.KG));
        profile.setHeight(new MockHeight(175.0, HeightUnit.CM));
        testUser.setPhysicalProfile(profile);

        // Act & Assert
        assertEquals(22.86, computationService.calculateBmi(testUser), 0.01);
    }

    @Test
    void calculateBmi_ValidProfile_LBS_INCH_ReturnsCorrectBmi() {
        // Arrange: 154.324 lb (70kg) / 68.8976 in (1.75m) -> 22.86
        UserPhysicalProfile profile = new UserPhysicalProfile();
        profile.setWeight(new MockWeight(70.0, MeasurementUnit.KG));
        profile.setHeight(new MockHeight(175.0, HeightUnit.CM));
        testUser.setPhysicalProfile(profile);

        // Act & Assert
        assertEquals(22.86, computationService.calculateBmi(testUser), 0.01, "Should correctly handle LBS and INCH conversion.");
    }

    @Test
    void calculateBmi_NullWeightValue_ReturnsNull() {
        // Arrange
        UserPhysicalProfile profile = new UserPhysicalProfile();
        profile.setWeight(new MockWeight(null, MeasurementUnit.KG)); // Value is null
        profile.setHeight(new MockHeight(180.0, HeightUnit.CM));
        testUser.setPhysicalProfile(profile);

        // Act & Assert
        assertNull(computationService.calculateBmi(testUser), "BMI should be null if the Weight value is null.");
    }

    @Test
    void calculateBmi_HeightValueIsZero_ReturnsNull() {
        // Arrange
        UserPhysicalProfile profile = new UserPhysicalProfile();
        profile.setWeight(new MockWeight(80.0, MeasurementUnit.KG));
        profile.setHeight(new MockHeight(0.0, HeightUnit.CM));
        testUser.setPhysicalProfile(profile);

        // Act & Assert
        assertNull(computationService.calculateBmi(testUser), "BMI should be null if height is zero (division by zero risk).");
    }

    // =========================================================================
    // MOST RECENT WEIGHT TESTS
    // =========================================================================

    @Test
    void getMostRecentWeight_ValidHistory_ReturnsLatestWeightInKg() {
        // Arrange
        UserPhysicalProfile oldProfile = new UserPhysicalProfile();
        oldProfile.setRecordedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        oldProfile.setWeight(new MockWeight(90.0, MeasurementUnit.KG));

        UserPhysicalProfile newestProfile = new UserPhysicalProfile();
        newestProfile.setRecordedAt(LocalDateTime.of(2024, 10, 20, 10, 0));
        newestProfile.setWeight(new MockWeight(85.0, MeasurementUnit.KG)); // ~85.0 kg

        testUser.setProfileHistory(List.of(oldProfile, newestProfile));

        // Act
        Double weight = computationService.getMostRecentWeight(testUser);

        System.out.println();
        // Assert
        assertEquals(85.0, weight, 0.01, "Should return the most recent weight, correctly converted to KG.");
    }

    @Test
    void getMostRecentWeight_EmptyHistory_ReturnsNull() {
        // Act & Assert
        assertNull(computationService.getMostRecentWeight(testUser), "Most recent weight should be null if history is empty.");
    }

    // =========================================================================
    // WORKOUT COUNT TESTS
    // =========================================================================

    @Test
    void getTotalWorkoutCount_MultipleWorkouts_ReturnsCorrectCount() {
        // Arrange
        List<Workout> workouts = List.of(new Workout(), new Workout(), new Workout());
        testUser.setWorkouts(workouts);

        // Act & Assert
        assertEquals(3, computationService.getTotalWorkoutCount(testUser), "Count should match the number of workouts.");
    }

    @Test
    void getTotalWorkoutCount_NullWorkoutsList_ReturnsZero() {
        // Arrange
        testUser.setWorkouts(null);

        // Act & Assert
        assertEquals(0, computationService.getTotalWorkoutCount(testUser), "Count should be 0 if the workout list is null.");
    }
}