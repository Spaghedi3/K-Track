package com.danis.backend.service.workflow;

import com.danis.backend.domain.model.entities.Exercise;
import com.danis.backend.domain.model.entities.User;
import com.danis.backend.domain.model.enums.ExerciseType;
import com.danis.backend.domain.repository.ExerciseRepository;
import com.danis.backend.domain.repository.UserRepository;
import com.danis.backend.dto.ExerciseCreateDTO;
import com.danis.backend.service.validation.ExerciseValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseWorkflowServiceImpl implements ExerciseWorkflowService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseValidationService validationService;
    private final UserRepository userRepository;

    @Override
    public Exercise createCustomExercise(ExerciseCreateDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Exercise exercise = Exercise.builder()
                .name(dto.getName())
                .imageUrl(dto.getImageUrl())
                .videoUrl(dto.getVideoUrl())
                .overview(dto.getOverview())
                .equipments(dto.getEquipments())
                .bodyParts(dto.getBodyParts())
                .targetMuscles(dto.getTargetMuscles())
                .secondaryMuscles(dto.getSecondaryMuscles())
                .keywords(dto.getKeywords())
                .instructions(dto.getInstructions())
                .exerciseTips(dto.getExerciseTips())
                .variations(dto.getVariations())
                .relatedExerciseIds(dto.getRelatedExerciseIds())
                .user(user)
                .type(ExerciseType.CUSTOM)
                .build();

        return exerciseRepository.save(exercise);
    }



    @Override
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    @Override
    public List<Exercise> getPreloadedExercises() {
        return exerciseRepository.findByType(ExerciseType.PRELOADED);
    }

    @Override
    public List<Exercise> getCustomExercises(Long userId) {
        return exerciseRepository.findByUserId(userId);
    }

    @Override
    public void deleteCustomExercise(Long exerciseId, Long userId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        if (exercise.getType() != ExerciseType.CUSTOM) {
            throw new RuntimeException("Not authorized");
        }


        if (exercise.getUser() == null || !exercise.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        exerciseRepository.delete(exercise);
    }

}
