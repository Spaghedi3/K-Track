package com.danis.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRecordDTO {
    private Long exerciseId;
    private String exerciseName;
    private double weight;
    private int reps;
    private double estimatedOneRepMax; // Using Epley formula
    private LocalDateTime achievedAt;
}