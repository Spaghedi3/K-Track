package com.danis.ktrack.dto.workout;

import com.danis.ktrack.domain.model.enums.WorkoutStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutResponse {

    private Long id;
    private String name;
    private LocalDate date;
    private WorkoutStatus status;
    private String notes;
    private double totalVolume;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMinutes;

    private Long userId;
    private String username;

    private Long templateId;
    private String templateName;

    private List<WorkoutExerciseResponse> exercises;

    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private String createdBy;
    private String lastModifiedBy;
}