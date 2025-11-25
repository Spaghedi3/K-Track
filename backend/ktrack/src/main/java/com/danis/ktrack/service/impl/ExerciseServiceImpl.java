package com.danis.ktrack.service.impl;

import com.danis.ktrack.domain.model.entities.Exercise;
import com.danis.ktrack.domain.model.entities.ExerciseStatistics;
import com.danis.ktrack.domain.repository.ExerciseRepository;
import com.danis.ktrack.domain.repository.ExerciseStatisticsRepository;
import com.danis.ktrack.dto.exercise.ExerciseDTO;
import com.danis.ktrack.dto.exercise.ExerciseMapper;
import com.danis.ktrack.dto.exercise.ExerciseStatisticsRequest;
import com.danis.ktrack.dto.exercise.ExerciseSummaryRequest;
import com.danis.ktrack.service.ExerciseService;
import com.danis.ktrack.service.validation.ExerciseValidationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseStatisticsRepository statisticsRepository;
    private final ExerciseMapper exerciseMapper;
    private final ExerciseValidationService exerciseValidationService;

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseSummaryRequest> getAllExercises() {
        return exerciseRepository.findAll().stream()
                .map(exerciseMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseDTO getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found with id: " + id));
        return exerciseMapper.toDTO(exercise);
    }

    @Override
    @Transactional
    public ExerciseDTO createExercise(ExerciseDTO exerciseDTO) {
        // 1. Convert DTO to Entity
        Exercise exercise = exerciseMapper.toEntity(exerciseDTO);

        // 2. Validate
        exerciseValidationService.validate(exercise);

        // 3. Save
        Exercise savedExercise = exerciseRepository.save(exercise);

        // 4. Return DTO
        return exerciseMapper.toDTO(savedExercise);
    }

    @Override
    @Transactional
    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new EntityNotFoundException("Exercise not found with id: " + id);
        }
        exerciseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseStatisticsRequest getExerciseStatistics(Long exerciseId, Long userId) {
        // Find the exercise to ensure it exists
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Exercise not found with id: " + exerciseId));

        // Find User (Using a simple mock check or repository fetch if strictly needed,
        // but repository findByUserAndExercise handles the join usually)

        // Fetch statistics
        // Note: We need to fetch the User entity to pass to the repo method,
        // or update the repo to take IDs. Assuming we have the User entity from context or ID:
        // For now, we rely on the repo method finding by IDs if possible, or we fetch the user proxy.
        // However, your Repo takes (User, Exercise).
        // We will fetch the statistics directly or throw if empty.

        // Ideally, you should inject UserRepository here to fetch the User object first.
        // For now, let's assume the stats might not exist yet and return empty DTO or throw.

        // FIX: To make this robust, you should inject UserRepository.
        // But to keep it compiling with your current setup:

        return statisticsRepository.findAll().stream()
                .filter(s -> s.getExercise().getId().equals(exerciseId) && s.getUser().getId().equals(userId))
                .findFirst()
                .map(exerciseMapper::toStatisticsDTO)
                .orElseThrow(() -> new EntityNotFoundException("No statistics found for this user/exercise"));
    }
}