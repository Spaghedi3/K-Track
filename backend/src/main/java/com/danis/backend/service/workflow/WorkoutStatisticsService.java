package com.danis.backend.service.workflow;

import com.danis.backend.domain.model.entities.*;
import com.danis.backend.domain.model.enums.WorkoutStatus;
import com.danis.backend.domain.repository.WorkoutRepository;
import com.danis.backend.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutStatisticsService {
    private final WorkoutRepository workoutRepository;

    /**
     * Get comprehensive workout statistics for a user
     */
    public UserWorkoutStatsDTO getUserWorkoutStats(Long userId) {
        // Get all finished workouts
        List<Workout> allWorkouts = workoutRepository.findByUserIdAndStatus(userId, WorkoutStatus.COMPLETED);

        // Get workouts from last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Workout> recentWorkouts = allWorkouts.stream()
                .filter(w -> w.getFinishedAt().isAfter(thirtyDaysAgo))
                .collect(Collectors.toList());

        return UserWorkoutStatsDTO.builder()
                // Overall stats
                .totalWorkouts(allWorkouts.size())
                .totalSets(calculateTotalSets(allWorkouts))
                .totalReps(calculateTotalReps(allWorkouts))
                .totalVolume(calculateTotalVolume(allWorkouts))
                .totalDuration(calculateTotalDuration(allWorkouts))

                // Recent stats
                .workoutsLast30Days(recentWorkouts.size())
                .volumeLast30Days(calculateTotalVolume(recentWorkouts))
                .averageWorkoutDuration(calculateAverageDuration(allWorkouts))

                // Streaks
                .currentStreak(calculateCurrentStreak(allWorkouts))
                .longestStreak(calculateLongestStreak(allWorkouts))

                // Exercise stats
                .totalUniqueExercises(calculateUniqueExercises(allWorkouts))
                .mostFrequentExercises(getMostFrequentExercises(allWorkouts, 5))

                // Muscle group distribution
                .muscleGroupDistribution(getMuscleGroupDistribution(allWorkouts))

                // Weekly breakdown
                .workoutsByDayOfWeek(getWorkoutsByDayOfWeek(allWorkouts))

                // Personal Records
                .personalRecords(getPersonalRecords(allWorkouts))

                .build();
    }

    /**
     * Get statistics for a specific time period
     */
    public PeriodStatsDTO getPeriodStats(Long userId, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Workout> workouts = workoutRepository.findByUserIdAndStatusAndFinishedAtAfter(
                userId, WorkoutStatus.COMPLETED, startDate
        );

        return PeriodStatsDTO.builder()
                .periodDays(days)
                .totalWorkouts(workouts.size())
                .totalSets(calculateTotalSets(workouts))
                .totalReps(calculateTotalReps(workouts))
                .totalVolume(calculateTotalVolume(workouts))
                .totalDuration(calculateTotalDuration(workouts))
                .averageWorkoutsPerWeek((double) workouts.size() / (days / 7.0))
                .volumeByWeek(getVolumeByWeek(workouts))
                .build();
    }

    /**
     * Get progress for a specific exercise
     */
    public ExerciseProgressDTO getExerciseProgress(Long userId, Long exerciseId) {
        List<Workout> workouts = workoutRepository.findByUserIdAndStatus(userId, WorkoutStatus.COMPLETED);

        List<ExerciseSessionDTO> sessions = new ArrayList<>();

        for (Workout workout : workouts) {
            for (WorkoutExercise we : workout.getExercises()) {
                if (we.getExercise().getId().equals(exerciseId)) {
                    sessions.add(mapToExerciseSession(we, workout));
                }
            }
        }

        sessions.sort(Comparator.comparing(ExerciseSessionDTO::getDate));

        if (sessions.isEmpty()) {
            return ExerciseProgressDTO.builder()
                    .exerciseId(exerciseId)
                    .totalSessions(0)
                    .sessions(new ArrayList<>())
                    .build();
        }

        return ExerciseProgressDTO.builder()
                .exerciseId(exerciseId)
                .exerciseName(sessions.get(0).getExerciseName())
                .totalSessions(sessions.size())
                .totalSets(sessions.stream().mapToInt(ExerciseSessionDTO::getSets).sum())
                .totalReps(sessions.stream().mapToInt(ExerciseSessionDTO::getReps).sum())
                .totalVolume(sessions.stream().mapToDouble(ExerciseSessionDTO::getVolume).sum())
                .maxWeightEver(sessions.stream()
                        .mapToDouble(ExerciseSessionDTO::getMaxWeight)
                        .max()
                        .orElse(0.0))
                .currentMaxWeight(sessions.get(sessions.size() - 1).getMaxWeight())
                .sessions(sessions)
                .build();
    }

    /**
     * Get all personal records for a user
     */
    public List<PersonalRecordDTO> getPersonalRecords(List<Workout> workouts) {
        Map<Long, PersonalRecordDTO> recordsMap = new HashMap<>();

        for (Workout workout : workouts) {
            for (WorkoutExercise we : workout.getExercises()) {
                Long exerciseId = we.getExercise().getId();

                for (WorkoutSet set : we.getSets()) {
                    if (!set.isCompleted() || set.getActualWeight() == null) {
                        continue;
                    }

                    double weight = set.getActualWeight();
                    int reps = set.getActualReps() != null ? set.getActualReps() : 0;

                    // One Rep Max estimation (Epley formula)
                    double estimatedOneRepMax = weight * (1 + reps / 30.0);

                    PersonalRecordDTO existing = recordsMap.get(exerciseId);

                    if (existing == null || estimatedOneRepMax > existing.getEstimatedOneRepMax()) {
                        recordsMap.put(exerciseId, PersonalRecordDTO.builder()
                                .exerciseId(exerciseId)
                                .exerciseName(we.getExercise().getName())
                                .weight(weight)
                                .reps(reps)
                                .estimatedOneRepMax(estimatedOneRepMax)
                                .achievedAt(workout.getFinishedAt())
                                .build());
                    }
                }
            }
        }

        return new ArrayList<>(recordsMap.values())
                .stream()
                .sorted(Comparator.comparing(PersonalRecordDTO::getEstimatedOneRepMax).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get muscle group statistics
     */
    public Map<String, MuscleGroupStatsDTO> getMuscleGroupDistribution(List<Workout> workouts) {
        Map<String, MuscleGroupStatsDTO> stats = new HashMap<>();

        for (Workout workout : workouts) {
            for (WorkoutExercise we : workout.getExercises()) {
                List<String> muscles = we.getExercise().getTargetMuscles();

                if (muscles != null) {
                    for (String muscle : muscles) {
                        stats.computeIfAbsent(muscle, m -> new MuscleGroupStatsDTO(m));

                        MuscleGroupStatsDTO muscleStats = stats.get(muscle);
                        muscleStats.incrementWorkoutCount();
                        muscleStats.addSets(we.getSets().size());
                        muscleStats.addVolume(calculateExerciseVolume(we));
                    }
                }
            }
        }

        return stats;
    }

    /**
     * Get workouts grouped by day of week
     */
    public Map<String, Integer> getWorkoutsByDayOfWeek(List<Workout> workouts) {
        Map<String, Integer> byDay = new LinkedHashMap<>();

        // Initialize all days
        for (DayOfWeek day : DayOfWeek.values()) {
            byDay.put(day.name(), 0);
        }

        for (Workout workout : workouts) {
            String day = workout.getFinishedAt().getDayOfWeek().name();
            byDay.put(day, byDay.get(day) + 1);
        }

        return byDay;
    }

    /**
     * Get most frequent exercises
     */
    public List<ExerciseFrequencyDTO> getMostFrequentExercises(List<Workout> workouts, int limit) {
        Map<Long, ExerciseFrequencyDTO> frequencyMap = new HashMap<>();

        for (Workout workout : workouts) {
            for (WorkoutExercise we : workout.getExercises()) {
                Long exerciseId = we.getExercise().getId();

                frequencyMap.computeIfAbsent(exerciseId, id ->
                        ExerciseFrequencyDTO.builder()
                                .exerciseId(id)
                                .exerciseName(we.getExercise().getName())
                                .imageUrl(we.getExercise().getImageUrl())
                                .count(0)
                                .totalSets(0)
                                .totalVolume(0.0)
                                .build()
                );

                ExerciseFrequencyDTO freq = frequencyMap.get(exerciseId);
                freq.incrementCount();
                freq.addSets(we.getSets().size());
                freq.addVolume(calculateExerciseVolume(we));
            }
        }

        return frequencyMap.values().stream()
                .sorted(Comparator.comparing(ExerciseFrequencyDTO::getCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Calculate current workout streak
     */
    public int calculateCurrentStreak(List<Workout> workouts) {
        if (workouts.isEmpty()) {
            return 0;
        }

        List<Workout> sorted = workouts.stream()
                .sorted(Comparator.comparing(Workout::getFinishedAt).reversed())
                .collect(Collectors.toList());

        Set<LocalDate> workoutDates = sorted.stream()
                .map(w -> w.getFinishedAt().toLocalDate())
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // Check if there's a workout today or yesterday
        if (!workoutDates.contains(today) && !workoutDates.contains(yesterday)) {
            return 0;
        }

        int streak = 0;
        LocalDate checkDate = workoutDates.contains(today) ? today : yesterday;

        while (workoutDates.contains(checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }

        return streak;
    }

    /**
     * Calculate longest workout streak
     */
    public int calculateLongestStreak(List<Workout> workouts) {
        if (workouts.isEmpty()) {
            return 0;
        }

        Set<LocalDate> workoutDates = workouts.stream()
                .map(w -> w.getFinishedAt().toLocalDate())
                .collect(Collectors.toSet());

        List<LocalDate> sortedDates = new ArrayList<>(workoutDates);
        sortedDates.sort(Comparator.naturalOrder());

        int longestStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < sortedDates.size(); i++) {
            LocalDate prev = sortedDates.get(i - 1);
            LocalDate curr = sortedDates.get(i);

            if (ChronoUnit.DAYS.between(prev, curr) == 1) {
                currentStreak++;
                longestStreak = Math.max(longestStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }

        return longestStreak;
    }

    /**
     * Get volume by week for the last N weeks
     */
    public List<WeeklyVolumeDTO> getVolumeByWeek(List<Workout> workouts) {
        Map<LocalDate, WeeklyVolumeDTO> weekMap = new HashMap<>();

        for (Workout workout : workouts) {
            LocalDate weekStart = workout.getFinishedAt().toLocalDate()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

            weekMap.computeIfAbsent(weekStart, date ->
                    WeeklyVolumeDTO.builder()
                            .weekStart(date)
                            .workoutCount(0)
                            .totalVolume(0.0)
                            .totalSets(0)
                            .build()
            );

            WeeklyVolumeDTO weekStats = weekMap.get(weekStart);
            weekStats.incrementWorkoutCount();
            weekStats.addVolume(calculateWorkoutVolume(workout));
            weekStats.addSets(calculateWorkoutSets(workout));
        }

        return weekMap.values().stream()
                .sorted(Comparator.comparing(WeeklyVolumeDTO::getWeekStart))
                .collect(Collectors.toList());
    }

    // ==================== Helper Methods ====================

    private int calculateTotalSets(List<Workout> workouts) {
        return workouts.stream()
                .mapToInt(this::calculateWorkoutSets)
                .sum();
    }

    private int calculateWorkoutSets(Workout workout) {
        return workout.getExercises().stream()
                .mapToInt(we -> we.getSets().size())
                .sum();
    }

    private int calculateTotalReps(List<Workout> workouts) {
        return workouts.stream()
                .mapToInt(this::calculateWorkoutReps)
                .sum();
    }

    private int calculateWorkoutReps(Workout workout) {
        return workout.getExercises().stream()
                .flatMap(we -> we.getSets().stream())
                .filter(WorkoutSet::isCompleted)
                .mapToInt(set -> set.getActualReps() != null ? set.getActualReps() : 0)
                .sum();
    }

    private double calculateTotalVolume(List<Workout> workouts) {
        return workouts.stream()
                .mapToDouble(this::calculateWorkoutVolume)
                .sum();
    }

    private double calculateWorkoutVolume(Workout workout) {
        return workout.getExercises().stream()
                .mapToDouble(this::calculateExerciseVolume)
                .sum();
    }

    private double calculateExerciseVolume(WorkoutExercise we) {
        return we.getSets().stream()
                .filter(WorkoutSet::isCompleted)
                .mapToDouble(set -> {
                    double weight = set.getActualWeight() != null ? set.getActualWeight() : 0.0;
                    int reps = set.getActualReps() != null ? set.getActualReps() : 0;
                    return weight * reps;
                })
                .sum();
    }

    private long calculateTotalDuration(List<Workout> workouts) {
        return workouts.stream()
                .filter(w -> w.getStartedAt() != null && w.getFinishedAt() != null)
                .mapToLong(w -> ChronoUnit.MINUTES.between(w.getStartedAt(), w.getFinishedAt()))
                .sum();
    }

    private double calculateAverageDuration(List<Workout> workouts) {
        List<Workout> withDuration = workouts.stream()
                .filter(w -> w.getStartedAt() != null && w.getFinishedAt() != null)
                .collect(Collectors.toList());

        if (withDuration.isEmpty()) {
            return 0.0;
        }

        return withDuration.stream()
                .mapToLong(w -> ChronoUnit.MINUTES.between(w.getStartedAt(), w.getFinishedAt()))
                .average()
                .orElse(0.0);
    }

    private int calculateUniqueExercises(List<Workout> workouts) {
        return (int) workouts.stream()
                .flatMap(w -> w.getExercises().stream())
                .map(we -> we.getExercise().getId())
                .distinct()
                .count();
    }

    private ExerciseSessionDTO mapToExerciseSession(WorkoutExercise we, Workout workout) {
        List<WorkoutSet> completedSets = we.getSets().stream()
                .filter(WorkoutSet::isCompleted)
                .collect(Collectors.toList());

        double maxWeight = completedSets.stream()
                .mapToDouble(set -> set.getActualWeight() != null ? set.getActualWeight() : 0.0)
                .max()
                .orElse(0.0);

        double avgWeight = completedSets.stream()
                .mapToDouble(set -> set.getActualWeight() != null ? set.getActualWeight() : 0.0)
                .filter(w -> w > 0)
                .average()
                .orElse(0.0);

        int totalReps = completedSets.stream()
                .mapToInt(set -> set.getActualReps() != null ? set.getActualReps() : 0)
                .sum();

        return ExerciseSessionDTO.builder()
                .date(workout.getFinishedAt().toLocalDate())
                .exerciseName(we.getExercise().getName())
                .sets(completedSets.size())
                .reps(totalReps)
                .maxWeight(maxWeight)
                .averageWeight(avgWeight)
                .volume(calculateExerciseVolume(we))
                .build();
    }
}