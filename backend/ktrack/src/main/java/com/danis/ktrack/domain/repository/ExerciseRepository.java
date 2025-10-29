package com.danis.ktrack.domain.repository;


import com.danis.ktrack.domain.model.entities.Exercise;
import com.danis.ktrack.domain.model.enums.MuscleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise,Long> {
    Optional<Exercise> findByMetadataNameIgnoreCase(String name);

    List<Exercise> findByPrimaryMuscleGroupsContains(MuscleGroup muscleGroup);

}
