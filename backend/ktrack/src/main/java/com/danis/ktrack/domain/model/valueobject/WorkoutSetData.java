package com.danis.ktrack.domain.model.valueobject;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetData {

    private Integer reps;

    @Embedded
    @AttributeOverrides({

            @AttributeOverride(name = "value", column = @Column(name = "weight_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "weight_unit"))
    })
    private Weight weight;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "distance_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "distance_unit"))
    })
    private Distance distance;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "duration_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "duration_unit"))
    })
    private Duration duration;

    @Embedded
     @AttributeOverrides({
         @AttributeOverride(name = "value", column = @Column(name = "rest_time_value")),
         @AttributeOverride(name = "unit", column = @Column(name = "rest_time_unit"))
     })
    private Duration restTime;
    private String notes;

}
