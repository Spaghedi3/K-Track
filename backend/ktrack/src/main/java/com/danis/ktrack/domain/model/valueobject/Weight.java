package com.danis.ktrack.domain.model.valueobject;


import com.danis.ktrack.domain.model.enums.MeasurementUnit;
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

    private Double value;

    @Enumerated(EnumType.STRING)
    private MeasurementUnit unit;


    public double toKilograms() {
        if (this.value == null || this.unit == null) {
            return 0.0;
        }

        if (this.unit == MeasurementUnit.KG) {
            return this.value;
        }
        return this.value * 0.453592;
    }
}