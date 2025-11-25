package com.danis.ktrack.restservices; // FIXED: Matches folder structure

import com.danis.ktrack.dto.exercise.ExerciseDTO;
import com.danis.ktrack.dto.exercise.ExerciseStatisticsRequest;
import com.danis.ktrack.dto.exercise.ExerciseSummaryRequest;
import com.danis.ktrack.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercises", description = "Endpoints for managing exercises and statistics")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    @Operation(summary = "Get all exercises", description = "Returns a summary list of all exercises")
    public ResponseEntity<List<ExerciseSummaryRequest>> getAllExercises() {
        return ResponseEntity.ok(exerciseService.getAllExercises());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exercise details", description = "Returns full details for a specific exercise")
    public ResponseEntity<ExerciseDTO> getExerciseById(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.getExerciseById(id));
    }

    @PostMapping
    @Operation(summary = "Create exercise", description = "Creates a new custom exercise")
    public ResponseEntity<ExerciseDTO> createExercise(@Valid @RequestBody ExerciseDTO exerciseDTO) {
        ExerciseDTO created = exerciseService.createExercise(exerciseDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete exercise", description = "Soft deletes an exercise")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/statistics")
    @Operation(summary = "Get exercise statistics", description = "Returns stats like total volume, PRs, etc.")
    public ResponseEntity<ExerciseStatisticsRequest> getExerciseStatistics(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(exerciseService.getExerciseStatistics(id, userId));
    }
}