package com.danis.ktrack.domain.model.entities;


import com.danis.ktrack.domain.model.valueobject.PersonalRecord;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="exercise_statistics")
public class ExerciseStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ElementCollection // 4.
    @CollectionTable(name = "personal_records", joinColumns = @JoinColumn(name = "statistics_id"))
    @OrderBy("achievedDate DESC")
    private List<PersonalRecord> personalRecords;

    @OneToMany(
            mappedBy = "stats",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("date DESC")
    private List<VolumeDataPoint> volumeHistory;

    private LocalDateTime lastPerformed;
    private int totalWorkouts;
    private int totalSets;
    private int totalReps;
    private double totalVolume;
}
