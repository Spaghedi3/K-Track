package com.danis.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseFrequencyDTO {
    private Long exerciseId;
    private String exerciseName;
    private String imageUrl;
    private int count; // Number of times performed
    private int totalSets;
    private double totalVolume;

    public void incrementCount() {
        this.count++;
    }

    public void addSets(int sets) {
        this.totalSets += sets;
    }

    public void addVolume(double volume) {
        this.totalVolume += volume;
    }
}