package com.danis.ktrack.domain.valueobject;


import com.danis.ktrack.domain.enums.MeasurementUnit;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Weight {

private double value;

@Enumerated(EnumType.STRING)
 private MeasurementUnit unit;
}
