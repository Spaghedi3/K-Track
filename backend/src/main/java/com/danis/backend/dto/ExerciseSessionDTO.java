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
public class ExerciseSessionDTO {
    private LocalDate date;
    private String exerciseName;
    private int sets;
    private int reps;
    private double maxWeight;
    private double averageWeight;
    private double volume;
}