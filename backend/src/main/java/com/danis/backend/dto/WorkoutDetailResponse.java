package com.danis.backend.dto;

import com.danis.backend.domain.model.enums.WorkoutStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class WorkoutDetailResponse {
    private Long id;
    private String templateName;
    private WorkoutStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private List<WorkoutExerciseResponse> exercises;
}