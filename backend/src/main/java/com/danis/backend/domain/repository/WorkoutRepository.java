package com.danis.backend.domain.repository;

import com.danis.backend.domain.model.entities.Workout;
import com.danis.backend.domain.model.enums.WorkoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    // NEW METHOD - use this one
    Optional<Workout> findFirstByUserIdAndStatusInOrderByStartedAtDesc(
            Long userId,
            List<WorkoutStatus> statuses
    );

    // Keep this for the validation check
    Optional<Workout> findFirstByUserIdAndStatus(Long userId, WorkoutStatus status);

    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId " +
            "AND w.status = :status AND w.finishedAt > :afterDate " +
            "ORDER BY w.finishedAt DESC")
    List<Workout> findByUserIdAndStatusAndFinishedAtAfter(
            @Param("userId") Long userId,
            @Param("status") WorkoutStatus status,
            @Param("afterDate") LocalDateTime afterDate
    );



    // Find workouts between dates
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId " +
            "AND w.finishedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY w.finishedAt DESC")
    List<Workout> findByUserIdAndFinishedAtBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Find workouts with exercises eagerly loaded (for statistics)
    @Query("SELECT DISTINCT w FROM Workout w " +
            "LEFT JOIN FETCH w.exercises we " +
            "LEFT JOIN FETCH we.exercise e " +
            "LEFT JOIN FETCH we.sets " +
            "WHERE w.user.id = :userId AND w.status = :status " +
            "ORDER BY w.finishedAt DESC")
    List<Workout> findByUserIdAndStatusWithExercises(
            @Param("userId") Long userId,
            @Param("status") WorkoutStatus status
    );

    // Count workouts by user
    long countByUserIdAndStatus(Long userId, WorkoutStatus status);

    // Find recent workouts
    List<Workout> findTop10ByUserIdAndStatusOrderByFinishedAtDesc(
            Long userId,
            WorkoutStatus status
    );

    List<Workout> findByUserIdAndStatus(Long userId, WorkoutStatus status);
}
