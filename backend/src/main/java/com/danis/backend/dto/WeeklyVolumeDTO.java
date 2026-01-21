package com.danis.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyVolumeDTO {
    private LocalDate weekStart;
    private int workoutCount;
    private double totalVolume;
    private int totalSets;

    public void incrementWorkoutCount() {
        this.workoutCount++;
    }

    public void addVolume(double volume) {
        this.totalVolume += volume;
    }

    public void addSets(int sets) {
        this.totalSets += sets;
    }
}