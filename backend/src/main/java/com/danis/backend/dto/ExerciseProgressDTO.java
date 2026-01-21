package com.danis.backend.dto;

import com.danis.backend.dto.ExerciseSessionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseProgressDTO {
    private Long exerciseId;
    private String exerciseName;
    private int totalSessions;
    private int totalSets;
    private int totalReps;
    private double totalVolume;
    private double maxWeightEver;
    private double currentMaxWeight;
    private List<ExerciseSessionDTO> sessions;
}