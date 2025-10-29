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

    // FIX 1: Changed from 'double' to wrapper 'Double' for null safety (CRITICAL!)
    private Double value;

    @Enumerated(EnumType.STRING)
    private MeasurementUnit unit;

    /**
     * Converts the weight value to kilograms (KG).
     * This method is required by the UserComputationService.
     * @return The weight in kilograms, or 0.0 if the value is null or invalid.
     */
    public double toKilograms() {
        // Safety checks before accessing 'value' or 'unit'
        if (this.value == null || this.unit == null) {
            return 0.0;
        }

        if (this.unit == MeasurementUnit.KG) {
            return this.value;
        }

        // Assuming MeasurementUnit has LBS (pounds) for the conversion
        // Conversion factor: 1 lb = 0.453592 kg
        return this.value * 0.453592;
    }
}