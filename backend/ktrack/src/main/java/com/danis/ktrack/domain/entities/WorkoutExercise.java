package com.danis.ktrack.domain.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="workout_exercises")
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    private int orderIndex;
    private String notes;
    private boolean personalRecordAchieved = false;

    @OneToMany(
            mappedBy = "workoutExercise",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("setNumber ASC")
    private List<WorkoutSet> sets;

}
