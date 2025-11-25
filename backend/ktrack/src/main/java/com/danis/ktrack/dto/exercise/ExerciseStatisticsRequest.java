package com.danis.ktrack.dto.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseStatisticsRequest {
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private Long userId;
    private String username;

    private int totalWorkouts;
    private int totalSets;
    private int totalReps;
    private double totalVolume;
    private LocalDateTime lastPerformed;

    private List<PersonalRecordRequest> personalRecords;
    private List<VolumeDataPointRequest> volumeHistory;
}