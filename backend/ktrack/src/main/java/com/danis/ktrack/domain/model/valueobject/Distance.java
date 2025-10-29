package com.danis.ktrack.domain.model.valueobject;


import com.danis.ktrack.domain.model.enums.DistanceUnit;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Distance {
    private double value;

    @Enumerated(EnumType.STRING)
    DistanceUnit unit;
}
