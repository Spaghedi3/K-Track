package com.danis.ktrack.domain.model.entities;


import com.danis.ktrack.domain.model.enums.SetType;
import com.danis.ktrack.domain.model.valueobject.WorkoutSetData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="workout_sets")
public class WorkoutSet {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_exercise_id", nullable = false)
    private WorkoutExercise workoutExercise;

    private int setNumber;

    @Enumerated(EnumType.STRING)
    private SetType setType;

    @Embedded
    private WorkoutSetData setData;

    private boolean isCompleted;
    private LocalDateTime completedAt;
}
