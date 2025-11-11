package com.danis.ktrack.repository;


import com.danis.ktrack.domain.model.entities.Exercise;
import com.danis.ktrack.domain.model.enums.MuscleGroup;
import com.danis.ktrack.domain.model.valueobject.ExerciseMetadata;
import com.danis.ktrack.domain.repository.ExerciseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ExerciseRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Test
    void findByMetadataNameIgnoreCase() {
        Exercise exercise = new Exercise();
        ExerciseMetadata metadata = new ExerciseMetadata();
        metadata.setName("Bench Press");
        exercise.setMetadata(metadata);
        exerciseRepository.save(exercise);

        Optional<Exercise> found = exerciseRepository.findByMetadataNameIgnoreCase("bench press");
        assertThat(found).isPresent();
    }

    @Test
    void findByPrimaryMuscleGroupsContains() {
        Exercise ex1 = new Exercise();
        ex1.setPrimaryMuscleGroups(List.of(MuscleGroup.CHEST));
        exerciseRepository.save(ex1);

        List<Exercise> found = exerciseRepository.findByPrimaryMuscleGroupsContains(MuscleGroup.CHEST);
        assertThat(found).isNotEmpty();
    }
}
