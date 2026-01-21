package com.danis.backend.domain.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workout_template_sets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutTemplateSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer reps;
    private Double weight;
    private Integer durationSeconds;
    private Double distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_exercise_id", nullable = false)
    private WorkoutTemplateExercise templateExercise;
}
