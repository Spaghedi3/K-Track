package com.danis.ktrack.domain.model.valueobject;


import com.danis.ktrack.domain.model.enums.ActivityLevel;
import com.danis.ktrack.domain.model.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPhysicalProfile {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "body_weight")),
            @AttributeOverride(name = "unit", column = @Column(name = "body_weight_unit"))
    })
    private Weight weight;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "height")),
            @AttributeOverride(name = "unit", column = @Column(name = "height_unit"))
    })
    private Height height;


    private Integer age;

    @Embedded
    private Gender gender;

    @Embedded
    private BodyFatPercentage bodyFatPercentage;

    @Embedded
    private ActivityLevel activityLevel;

    private LocalDateTime recordedAt;
}
