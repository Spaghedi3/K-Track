package com.danis.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWorkoutStatsDTO {
    // Overall statistics
    private int totalWorkouts;
    private int totalSets;
    private int totalReps;
    private double totalVolume; // kg
    private long totalDuration; // minutes

    // Recent activity
    private int workoutsLast30Days;
    private double volumeLast30Days;
    private double averageWorkoutDuration; // minutes

    // Streaks
    private int currentStreak; // days
    private int longestStreak; // days

    // Exercise diversity
    private int totalUniqueExercises;
    private List<ExerciseFrequencyDTO> mostFrequentExercises;

    // Muscle group distribution
    private Map<String, MuscleGroupStatsDTO> muscleGroupDistribution;

    // Weekly patterns
    private Map<String, Integer> workoutsByDayOfWeek;

    // Personal Records
    private List<PersonalRecordDTO> personalRecords;
}