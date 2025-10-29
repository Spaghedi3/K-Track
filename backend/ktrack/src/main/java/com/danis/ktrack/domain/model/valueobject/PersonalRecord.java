package com.danis.ktrack.domain.model.valueobject;


import com.danis.ktrack.domain.model.enums.PRType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalRecord {

    private String exerciseId;

    @Enumerated(EnumType.STRING)
   private PRType recordType;

    double value;

    private LocalDateTime achievedDate;
   private String workoutId;
}
