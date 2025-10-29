package com.danis.ktrack.domain.model.entities;


import com.danis.ktrack.domain.model.valueobject.VolumeMetrics;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="volume_data_points")
public class VolumeDataPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statistics_id", nullable = false)
    private ExerciseStatistics stats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @Embedded
    private VolumeMetrics metrics;

    private LocalDate date;



}
