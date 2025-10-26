package com.danis.ktrack.domain.valueobject;


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
            // Renames 'value' field from Weight VO to 'weight_value' column
            @AttributeOverride(name = "value", column = @Column(name = "weight_value")),
            // Renames 'unit' field from Weight VO to 'weight_unit' column
            @AttributeOverride(name = "unit", column = @Column(name = "weight_unit"))
    })
    private Weight weight;

    @Embedded
    @AttributeOverrides({
            // Renames 'value' field from Distance VO to 'distance_value' column
            @AttributeOverride(name = "value", column = @Column(name = "distance_value")),
            // Rena-mes 'unit' field from Distance VO to 'distance_unit' column
            @AttributeOverride(name = "unit", column = @Column(name = "distance_unit"))
    })
    private Distance distance;

    @Embedded
    @AttributeOverrides({
            // Renames 'value' field from Duration VO to 'duration_value' column
            @AttributeOverride(name = "value", column = @Column(name = "duration_value")),
            // Renames 'unit' field from Duration VO to 'duration_unit' column
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
