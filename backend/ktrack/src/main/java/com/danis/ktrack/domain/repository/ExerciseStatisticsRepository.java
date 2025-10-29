package com.danis.ktrack.domain.repository;

import com.danis.ktrack.domain.model.entities.Exercise;
import com.danis.ktrack.domain.model.entities.ExerciseStatistics;
import com.danis.ktrack.domain.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseStatisticsRepository extends JpaRepository<ExerciseStatistics, Long> {
    Optional<ExerciseStatistics> findByUserAndExercise(User user, Exercise exercise);
}