package com.danis.ktrack.domain.valueobject;

import com.danis.ktrack.domain.enums.HeightUnit;
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
public class Height {

    private Double value;

    @Enumerated(EnumType.STRING)
   private HeightUnit unit;

    public double toCentimeters() {
        return unit == HeightUnit.CM ? value : value * 2.54;
    }

    public double toInches() {
        return unit == HeightUnit.INCH ? value : value / 2.54;
    }
}
