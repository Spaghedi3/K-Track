package com.danis.ktrack.domain.repository;


import com.danis.ktrack.domain.model.entities.User;
import com.danis.ktrack.domain.model.entities.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout,Long>
{
    @Query("SELECT w FROM Workout w " +
            "LEFT JOIN FETCH w.workoutExercises we " +
            "WHERE w.id = :workoutId")
    Optional<Workout> findByIdWithExercises(@Param("workoutId") Long workoutId);

}
