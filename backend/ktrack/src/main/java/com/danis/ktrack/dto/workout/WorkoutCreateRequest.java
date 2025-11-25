package com.danis.ktrack.dto.workout;

import com.danis.ktrack.domain.model.enums.WorkoutStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutCreateRequest {

    @NotBlank(message = "Workout name is required")
    private String name;

    @NotNull(message = "User ID is required")
    private Long userId;

    private LocalDate date;

    private WorkoutStatus status;

    private String notes;

    private Long templateId; // Optional: if starting from template
}