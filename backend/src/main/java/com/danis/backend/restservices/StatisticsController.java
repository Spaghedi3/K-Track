package com.danis.backend.restservices;


import com.danis.backend.dto.*;
import com.danis.backend.service.workflow.WorkoutStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final com.danis.backend.service.workflow.WorkoutStatisticsService statisticsService;

    /**
     * Get comprehensive workout statistics for the current user
     * GET /api/statistics/overview
     */
    @GetMapping("/overview")
    public UserWorkoutStatsDTO getOverview() {
        Long userId = getCurrentUserId();
        return statisticsService.getUserWorkoutStats(userId);
    }

    /**
     * Get statistics for a specific period
     * GET /api/statistics/period?days=30
     */
    @GetMapping("/period")
    public PeriodStatsDTO getPeriodStats(@RequestParam(defaultValue = "30") int days) {
        Long userId = getCurrentUserId();
        return statisticsService.getPeriodStats(userId, days);
    }

    /**
     * Get progress for a specific exercise
     * GET /api/statistics/exercise/{exerciseId}
     */
    @GetMapping("/exercise/{exerciseId}")
    public ExerciseProgressDTO getExerciseProgress(@PathVariable Long exerciseId) {
        Long userId = getCurrentUserId();
        return statisticsService.getExerciseProgress(userId, exerciseId);
    }

    /**
     * Get personal records for all exercises
     * GET /api/statistics/personal-records
     */
    @GetMapping("/personal-records")
    public java.util.List<PersonalRecordDTO> getPersonalRecords() {
        Long userId = getCurrentUserId();
        UserWorkoutStatsDTO stats = statisticsService.getUserWorkoutStats(userId);
        return stats.getPersonalRecords();
    }

    /**
     * Get muscle group distribution
     * GET /api/statistics/muscle-groups
     */
    @GetMapping("/muscle-groups")
    public java.util.Map<String, MuscleGroupStatsDTO> getMuscleGroupStats() {
        Long userId = getCurrentUserId();
        UserWorkoutStatsDTO stats = statisticsService.getUserWorkoutStats(userId);
        return stats.getMuscleGroupDistribution();
    }

    private Long getCurrentUserId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}