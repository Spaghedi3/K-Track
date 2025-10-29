package com.danis.ktrack.service.computation;

import com.danis.ktrack.domain.model.entities.*;
import com.danis.ktrack.domain.model.enums.PRType;
import com.danis.ktrack.domain.model.valueobject.PersonalRecord;
import com.danis.ktrack.domain.model.valueobject.VolumeMetrics;
import com.danis.ktrack.domain.model.valueobject.Weight;
import com.danis.ktrack.domain.model.valueobject.WorkoutSetData;
import com.danis.ktrack.domain.repository.ExerciseStatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
public class StatisticsComputationServiceImpl implements StaticticsComputationService{

    private final ExerciseStatisticsRepository statsRepository;

    public StatisticsComputationServiceImpl(ExerciseStatisticsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    @Transactional
    public void updateStatisticsFromSet(WorkoutSet completedSet) {
        if (!isValidForProcessing(completedSet)) {
            log.warn("WorkoutSet {} is not valid for statistics processing.",
                    (completedSet != null ? completedSet.getId() : "null"));
            return;
        }

        WorkoutExercise workoutExercise = completedSet.getWorkoutExercise();
        Workout workout = workoutExercise.getWorkout();
        User user = workout.getUser();
        Exercise exercise = workoutExercise.getExercise();
        WorkoutSetData setData = completedSet.getSetData();
        LocalDateTime completedAt = completedSet.getCompletedAt();

        ExerciseStatistics stats = findOrCreateStatistics(user, exercise);


        stats.setTotalSets(stats.getTotalSets() + 1);
        stats.setLastPerformed(completedAt);

        if (isWeightAndRepSet(setData)) {
            int reps = setData.getReps();
            double weight = setData.getWeight().getValue();
            double setVolume = reps * weight;


            stats.setTotalReps(stats.getTotalReps() + reps);
            stats.setTotalVolume(stats.getTotalVolume() + setVolume);

            updateVolumeDataPoint(stats, workout, reps, setVolume, completedAt.toLocalDate());

            updatePersonalRecords(stats, reps, weight, completedAt, exercise.getId(), workout.getId());
        }

        statsRepository.save(stats);
        log.info("Updated statistics for user {} and exercise {}", user.getId(), exercise.getId());
    }



    private void updateVolumeDataPoint(ExerciseStatistics stats, Workout workout, int reps, double setVolume, LocalDate date) {
        Optional<VolumeDataPoint> existingDp = stats.getVolumeHistory().stream()
                .filter(dp -> dp.getWorkout().getId().equals(workout.getId()))
                .findFirst();

        if (existingDp.isPresent()) {
            VolumeDataPoint dp = existingDp.get();
            VolumeMetrics metrics = dp.getMetrics();
            if (metrics == null) {
                metrics = new VolumeMetrics(0.0, 0, 0, null);
            }
            metrics.setTotalVolume(metrics.getTotalVolume() + setVolume);
            metrics.setTotalSets(metrics.getTotalSets() + 1);
            metrics.setTotalReps(metrics.getTotalReps() + reps);
            dp.setMetrics(metrics);
        } else {
            VolumeMetrics newMetrics = new VolumeMetrics(setVolume, 1, reps, null);
            VolumeDataPoint newDp = new VolumeDataPoint();
            newDp.setStats(stats);
            newDp.setWorkout(workout);
            newDp.setDate(date);
            newDp.setMetrics(newMetrics);
            stats.getVolumeHistory().add(newDp);
        }
    }

    private boolean isValidForProcessing(WorkoutSet set) {
        return set != null &&
                set.isCompleted() &&
                set.getCompletedAt() != null &&
                set.getWorkoutExercise() != null &&
                set.getWorkoutExercise().getWorkout() != null &&
                set.getWorkoutExercise().getWorkout().getUser() != null &&
                set.getWorkoutExercise().getExercise() != null &&
                set.getSetData() != null;
    }


    private boolean isWeightAndRepSet(WorkoutSetData setData) {
        return setData.getReps() != null &&
                setData.getReps() > 0 &&
                setData.getWeight() != null &&
                setData.getWeight().getValue() > 0;
    }

    private ExerciseStatistics findOrCreateStatistics(User user, Exercise exercise) {
        return statsRepository.findByUserAndExercise(user, exercise)
                .orElseGet(() -> {
                    log.info("Creating new statistics for user {} and exercise {}", user.getId(), exercise.getId());
                    ExerciseStatistics newStats = new ExerciseStatistics();
                    newStats.setUser(user);
                    newStats.setExercise(exercise);
                    newStats.setPersonalRecords(new ArrayList<>());
                    newStats.setVolumeHistory(new ArrayList<>());
                    newStats.setTotalSets(0);
                    newStats.setTotalReps(0);
                    newStats.setTotalVolume(0.0);
                    newStats.setTotalWorkouts(0);
                    return newStats;
                });
    }


    private void updatePersonalRecords(ExerciseStatistics stats, int reps, double weight, LocalDateTime date,
                                       Long exerciseId, Long workoutId) {


        double estimated1RM = calculateEpley1RM(reps, weight);

        Optional<PersonalRecord> existingPR = stats.getPersonalRecords().stream()
                .filter(pr -> pr.getType() == PRType.MAX_WEIGHT)
                .findFirst();

        if (existingPR.isPresent()) {
            if (estimated1RM > existingPR.get().getValue()) {
                // New record
                PersonalRecord pr = existingPR.get();
                pr.setValue(estimated1RM);
                pr.setAchievedDate(date);
                pr.setWorkoutId(String.valueOf(workoutId)); // Update workout ID
                log.info("New 1RM PR achieved: {}!", estimated1RM);
            }
        } else {
            PersonalRecord newPR = new PersonalRecord(
                    String.valueOf(exerciseId),
                    PRType.MAX_WEIGHT,
                    estimated1RM,
                    date,
                    String.valueOf(workoutId)
            );
            stats.getPersonalRecords().add(newPR);
            log.info("First 1RM PR set: {}!", estimated1RM);
        }
    }

    private double calculateEpley1RM(int reps, double weight) {
        if (reps == 1) {
            return weight;
        }
        return weight * (1 + (double) reps / 30.0);
    }
}


