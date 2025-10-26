package com.danis.ktrack.domain.valueobject;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.Duration;
import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPeriod {


    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Duration getDuration() {
        return Duration.between(startTime,endTime);
    }
}
