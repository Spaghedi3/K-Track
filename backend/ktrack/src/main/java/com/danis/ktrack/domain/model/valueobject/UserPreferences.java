package com.danis.ktrack.domain.model.valueobject;


import com.danis.ktrack.domain.model.enums.HeightUnit;
import com.danis.ktrack.domain.model.enums.MeasurementUnit;
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
public class UserPreferences {
    @Enumerated(EnumType.STRING)
    private MeasurementUnit defaultWeightUnit;

    @Enumerated(EnumType.STRING)
    private HeightUnit defaultHeightUnit;

    private String theme;
}
