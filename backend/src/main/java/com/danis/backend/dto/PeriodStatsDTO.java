package com.danis.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodStatsDTO {
    private int periodDays;
    private int totalWorkouts;
    private int totalSets;
    private int totalReps;
    private double totalVolume;
    private long totalDuration;
    private double averageWorkoutsPerWeek;
    private List<WeeklyVolumeDTO> volumeByWeek;
}