package com.danis.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MuscleGroupStatsDTO {
    private String muscleName;
    private int workoutCount;
    private int totalSets;
    private double totalVolume;

    public MuscleGroupStatsDTO(String muscleName) {
        this.muscleName = muscleName;
        this.workoutCount = 0;
        this.totalSets = 0;
        this.totalVolume = 0.0;
    }

    public void incrementWorkoutCount() {
        this.workoutCount++;
    }

    public void addSets(int sets) {
        this.totalSets += sets;
    }

    public void addVolume(double volume) {
        this.totalVolume += volume;
    }
}