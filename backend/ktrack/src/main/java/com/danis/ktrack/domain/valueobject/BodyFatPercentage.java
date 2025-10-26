package com.danis.ktrack.domain.valueobject;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Embeddable
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class BodyFatPercentage {
    private double value;


}
