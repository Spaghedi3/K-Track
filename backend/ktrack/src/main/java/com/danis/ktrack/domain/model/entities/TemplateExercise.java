package com.danis.ktrack.domain.model.entities;


import com.danis.ktrack.domain.model.valueobject.Duration;
import com.danis.ktrack.domain.model.valueobject.Weight;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="template_exercises")
public class TemplateExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private WorkoutTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    private int orderIndex;
    private int suggestedSets;
    private String suggestedReps;

    @Embedded
    private Weight suggestedWeight;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "rest_time_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "rest_time_unit"))
    })
    private Duration suggestedRestTime;

    private String notes;
}
