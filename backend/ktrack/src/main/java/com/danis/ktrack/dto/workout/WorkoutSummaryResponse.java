package com.danis.ktrack.dto.workout;

import com.danis.ktrack.domain.model.enums.WorkoutStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSummaryResponse {

    private Long id;
    private String name;
    private LocalDate date;
    private WorkoutStatus status;

    // Time Summary
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMinutes;

    // Volume Summary
    private double totalVolume;
    private int totalSets;
    private int totalReps;

    // Exercise Summary
    private int totalExercises;
    private int completedExercises;

    // Personal Records
    private int personalRecordsAchieved;

    // Exercise Breakdown (exercise name -> sets completed)
    private Map<String, Integer> exerciseBreakdown;

    // Muscle Groups Targeted
    private Map<String, Integer> muscleGroupsTargeted;
}