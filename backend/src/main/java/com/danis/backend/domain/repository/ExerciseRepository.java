package com.danis.backend.domain.repository;

import com.danis.backend.domain.model.entities.Exercise;
import com.danis.backend.domain.model.enums.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByType(ExerciseType type);
    List<Exercise> findByUserId(Long userId);
    boolean existsByExerciseId(String exerciseId);

    Exercise findByExerciseId(String exerciseId);
}
