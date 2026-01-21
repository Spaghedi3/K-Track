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
public class ExerciseResponseDTO {
    private Long id;
    private String exerciseId;
    private String name;
    private String imageUrl;
    private String videoUrl;
    private String overview;
    private List<String> equipments;
    private List<String> bodyParts;
    private List<String> targetMuscles;
    private List<String> secondaryMuscles;
    private List<String> keywords;
    private List<String> instructions;
    private List<String> exerciseTips;
    private List<String> variations;
    private List<String> relatedExerciseIds;
    private String type;
}
