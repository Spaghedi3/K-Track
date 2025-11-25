package com.danis.ktrack.domain.model.valueobject;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class
VolumeMetrics {

    private double totalVolume;

    private Integer totalSets;

    private Integer totalReps;

    private Integer Weight;
}
