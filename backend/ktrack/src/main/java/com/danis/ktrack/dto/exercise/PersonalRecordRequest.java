package com.danis.ktrack.dto.exercise;

import com.danis.ktrack.domain.model.enums.PRType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRecordRequest {
    private Long exerciseId;
    private Long userId;
    private Long workoutId;     // Added
    private PRType type;        // Changed from String to Enum to match Mapper
    private Double value;       // Added
    private LocalDateTime achievedDate; // Added
}