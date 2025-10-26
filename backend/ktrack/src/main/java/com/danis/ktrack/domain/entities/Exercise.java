package com.danis.ktrack.domain.entities;


import com.danis.ktrack.domain.enums.ExerciseCategory;
import com.danis.ktrack.domain.enums.ExerciseType;
import com.danis.ktrack.domain.enums.MuscleGroup;
import com.danis.ktrack.domain.valueobject.ExerciseMetadata;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ExerciseMetadata metadata;

    @ElementCollection(targetClass = MuscleGroup.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "exercise_primary_muscles", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "muscle_group")
    private List<MuscleGroup> primaryMuscleGroups;

    @ElementCollection(targetClass = MuscleGroup.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "exercise_secondary_muscles", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "muscle_group")
    private List<MuscleGroup> secondaryMuscleGroups;

    @Enumerated(EnumType.STRING)
    private ExerciseCategory category;

    @Enumerated(EnumType.STRING)
    private ExerciseType type;

    private boolean isCustom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    private boolean isDeleted = false;
    private LocalDateTime createdAt;
}
