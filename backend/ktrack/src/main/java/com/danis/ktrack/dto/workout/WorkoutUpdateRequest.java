package com.danis.ktrack.dto.workout;

import com.danis.ktrack.domain.model.enums.WorkoutStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutUpdateRequest {

    @NotBlank(message = "Workout name is required")
    private String name;

    private LocalDate date;

    private WorkoutStatus status;

    private String notes;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}