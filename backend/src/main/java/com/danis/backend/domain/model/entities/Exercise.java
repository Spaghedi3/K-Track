package com.danis.backend.domain.model.entities;

import com.danis.backend.domain.model.enums.ExerciseType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // External ID from AscendAPI
    @Column(unique = true)
    private String exerciseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    private String videoUrl;
    private String overview;

    @ElementCollection
    private List<String> equipments;

    @ElementCollection
    private List<String> bodyParts;

    @ElementCollection
    private List<String> targetMuscles;

    @ElementCollection
    private List<String> secondaryMuscles;

    @ElementCollection
    private List<String> keywords;

    @ElementCollection
    private List<String> instructions;

    @ElementCollection
    private List<String> exerciseTips;

    @ElementCollection
    private List<String> variations;

    @ElementCollection
    private List<String> relatedExerciseIds;

    @Enumerated(EnumType.STRING)
    private ExerciseType type; // PRELOADED or CUSTOM


}
